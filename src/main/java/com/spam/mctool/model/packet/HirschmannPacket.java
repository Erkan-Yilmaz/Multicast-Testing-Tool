package com.spam.mctool.model.packet;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.zip.DataFormatException;

/**
 * @author konne
 *
 * This class allows usage of the Hirschmann Package structure,
 * making comunication with the Hirschmann Application possible.
 * 
 * The following definitions are in pseudo C
 * 
 * The package starts with a null terminated string as a header
 * 
 *      #define HEADER "Hirschmann IP Test-Multicast"
 * 
 * The package format is BIG ENDIAN and defines the following fields
 * 
 *      struct Fields {
 *		    UINT8        header[sizeof(HEADER)];	// header
 *		    UINT16       senderId;
 *		    UINT32       txPktCnt;   // That's the number of UDP packets which are sent == sequenceNumber
 *		    UINT16       txPktRate;  // This is packet rate in pps the user has specified for the transmitter == configuredPacketsPerSecond
 *		    UINT8        ttl; 		 // start TTL. This start TTL compared with the TTL 
 *									 // in the IP header gives us the number of the routers
 *                                   // in the network the packed has passed
 *                                   // This field is not used by this class
 *		    BOOL         reset;      // This byte resets some counters for example NumOfInterrup, 
 *									 // MaxInterruptTime and NumOfLostPkt
 *                                   // This field is not used by this class
 *		    UINT16       checksum;   // checksum
 *		};
 *
 * The checksum is rather weird. Even though the package format is in 
 * big endian, the checksum is computed on the structure in the 
 * sender's cpu format.
 *
 * Since there is no way to know what kind of endian is used by the
 * sender's cpu, or even worse the receiver's cpu, the checksum is
 * calculated in the most common format Little Endian.
 * On the receiver side, packets with correct checksums in either 
 * Little Endian or Big Endian encodings will be accepted.
 */
public class HirschmannPacket implements Packet {
	/**
	 * @param data	the data to be set (starts reading at the current position)
	 * @throws DataFormatException thrown if badly formated data is passed
	 */
	public void fromByteArray(ByteBuffer data) throws DataFormatException 
	{
		minSize = 0;
		senderID = 0;
		configuredPacketsPerSeconds = 0;
		sequenceNumber = 0;
		
		try {
			data.order(ByteOrder.BIG_ENDIAN);
			
			// create data buffer to check for big endian checksums
			ByteBuffer bigEndianCheckData = data.duplicate();
			bigEndianCheckData.limit(CONSTSIZE-2);
			
			// skip header
			data.position(data.position()+HEADER.length);
			
			// read fields
			setSenderId(data.getShort() & 0xFFFF);
			setSequenceNumber(data.getInt() & 0xFFFFFFFFL);
			setConfiguredPacketsPerSecond(data.getShort() & 0xFFFF);
			short ttl = (short)(data.get() & 0xFF);
			long reset = data.getInt() & 0xFFFFFFFFL;
			short check = data.getShort();
			
			// check the checksum
			ByteBuffer checkData = createUncheckedByteArray(ttl,reset,ByteOrder.LITTLE_ENDIAN);
			if(generateChecksum(checkData,ByteOrder.LITTLE_ENDIAN) != check) {
				if(generateChecksum(bigEndianCheckData,ByteOrder.BIG_ENDIAN) != check) {
					throw new DataFormatException("Invalid checksum");
				}
			}
		} catch(BufferUnderflowException e) {
			throw new DataFormatException("Illegal Hirschmann Package Format");
		}
	}
	
	private static short generateChecksum(ByteBuffer data, ByteOrder order) {
	    long sum = 0;
	    
	    data.order(order);
	    
	    // Our algorithm is simple, using a 32 bit accumulator (sum),
	    // we add sequential 16 bit words to it, and at the end, fold
	    // back all the carry bits from the top 16 bits into the lower
	    // 16 bits
	    while (data.remaining() > 1) {
	    	int c = data.getShort() & 0xFFFF;
    		sum += c;	    
    	}
	    
	    // if buffer can't be split in shorts
	    // imagine appending 0x00 to the buffer
	    if (data.limit() % 2 == 1) {
	        sum += (data.get(data.limit()-1) & 0xFF);
	    }
	    	    
	    // add hi 16 to low 16
	    sum = (sum >> 16) + (sum & 0xFFFF); 
	    // add carry
	    sum += (sum >> 16);                 
	    
	    // truncate to 16 bits
	    return (short)(~sum);
	}
	
	private static final byte[] HEADER = {'H','i','r','s','c','h','m','a','n','n',
								  ' ','I','P',' ','T','e','s','t','-','M',
								  'u','l','t','i','c','a','s','t','\0'};
	
	private static final int CONSTSIZE = HEADER.length+2+4+2+1+4+2;
	
	/**
	 * @return returns the size 
	 */
	public long getSize() {
		return CONSTSIZE<minSize ? minSize : CONSTSIZE;
	}
	
	private ByteBuffer createUncheckedByteArray(short ttl, long reset, ByteOrder endian) {
		ByteBuffer data = ByteBuffer.allocate((int) getSize());
		data.order(endian);
		data.put(HEADER);
		
		data.putShort((short)getSenderId());
		data.putInt((int)getSequenceNumber());
		data.putShort((short)getConfiguredPacketsPerSecond());
		data.put((byte)ttl);
		data.putInt((int)reset);
		
		data.rewind();
		
		return data;
	}
	
	
	/**
	 * @return	the byte representation of this packet
	 */
	public ByteBuffer toByteArray() 
	{
		ByteBuffer checkData = createUncheckedByteArray((short) 0xFF,0, ByteOrder.LITTLE_ENDIAN);
		ByteBuffer data = createUncheckedByteArray((short) 0xFF,0, ByteOrder.BIG_ENDIAN);
		data.putShort(CONSTSIZE-2,generateChecksum(checkData,ByteOrder.LITTLE_ENDIAN));
		
		return data;
	}
	
	/**
	 * @return the senderID
	 */
	public long getSenderId() {
		return senderID;
	}
	/**
	 * @param senderID the senderID to set
	 */
	public void setSenderId(long senderID) {
		this.senderID = senderID;
	}
	/**
	 * @return the configuredPacketsPerSeconds
	 */
	public long getConfiguredPacketsPerSecond() {
		return configuredPacketsPerSeconds;
	}
	/**
	 * @param configuredPacketsPerSeconds the configuredPacketsPerSeconds to set
	 */
	public void setConfiguredPacketsPerSecond(long configuredPacketsPerSeconds) {
		this.configuredPacketsPerSeconds = configuredPacketsPerSeconds;
	}
	/**
	 * @return the senderMeasuredPacketRate
	 */
	public long getSenderMeasuredPacketRate() {
		return 0;
	}
	/**
	 * @param senderMeasuredPacketRate the senderMeasuredPacketRate to set
	 */
	public void setSenderMeasuredPacketRate(long senderMeasuredPacketRate) 
	{}
	/**
	 * @return the sequenceNumber
	 */
	public long getSequenceNumber() {
		return sequenceNumber;
	}
	/**
	 * @param sequenceNumber the sequenceNumber to set
	 */
	public void setSequenceNumber(long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	/**
	 * @return the dispatchTime
	 */
	public long getDispatchTime() {
		return 0;
	}
	/**
	 * @param dispatchTime the dispatchTime to set
	 */
	public void setDispatchTime(long dispatchTime) {}
	
	/**
	 * @param data  The payload's data
	 */
	public void setPayload(byte[] data) {}
	
	/**
	 * @return      Returns the payload's data
	 */
	public byte[] getPayload() {
	    return new byte[0];
	}

	/**
	 * Set the size of the package.
	 * Package may be bigger, e.g. if size is set to 1
	 * because we need to send the header and other stuff
	 * 
	 * @param size  The packets minimum size
	 */
	public void setMinimumSize(long size) {
		minSize = size;
	}
	
	public static boolean isCorrectHeader(ByteBuffer data) {
		if(data.remaining() >= HEADER.length) {
			byte[] head = new byte[HEADER.length];
			data.mark();
			data.get(head);
			data.reset();
			
			if(Arrays.equals(HEADER,head)) {
				return true;
			}
		}
		return false;
	}
	
	private long minSize;
	private long senderID;
	private long configuredPacketsPerSeconds;
	private long sequenceNumber;
}
