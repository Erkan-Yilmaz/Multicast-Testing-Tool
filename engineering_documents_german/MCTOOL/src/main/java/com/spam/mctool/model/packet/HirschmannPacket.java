package com.spam.mctool.model.packet;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.zip.DataFormatException;

/**
 * @author konne
 *
 * Represents a packet of the binary Hirschmann format as specified below.
 * 
 * The following specification is written in a C like way.
 * The package starts with a null terminated ASCII string as a header
 * 
 * <code>
 * #define HEADER "Hirschmann IP Test-Multicast"
 * </code>
 * 
 * The package format is BIG ENDIAN and defines the following fields
 * 
 * <code>
 * struct Fields {
 *     // Header as defined above
 *     UINT8        header[sizeof(HEADER)];    
 *     
 *     // Sender Id, just like the one in the packet class
 *     UINT16       senderId;
 *     
 *     // That's the number of UDP packets which are sent == sequenceNumber
 *     UINT32       txPktCnt;   
 *     
 *     // This is packet rate in pps the user has specified for the transmitter == configuredPacketsPerSecond
 *     UINT16       txPktRate;  
 *     
 *     // Start TTL. This start TTL compared with the TTL 
 *     // in the IP header gives us the number of the routers
 *     // in the network the packed has passed (hops)
 *     // This field is not used in this class
 *     UINT8        ttl;
 *     
 *     // This byte resets some counters for example NumOfInterrup, 
 *     // MaxInterruptTime and NumOfLostPkt
 *     // This field is not used by this class
 *     BOOL         reset;
 *     
 *     // Checksum as defined below
 *     UINT16       checksum;   
 * };
 * </code>
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
public final class HirschmannPacket implements Packet {
    /**
     * @see com.spam.mctool.model.packet.Packet#fromByteArray(ByteBuffer)
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
            bigEndianCheckData.limit(ENTIRE_SIZE-CHECKSUM_SIZE);
            
            // skip header
            data.position(data.position()+HEADER.length);
            
            // read fields
            setSenderId(data.getShort() & SHORT_MASK);
            setSequenceNumber(data.getInt() & INT_MASK);
            setConfiguredPacketsPerSecond(data.getShort() & SHORT_MASK);
            short ttl = (short)(data.get() & BYTE_MASK);
            long reset = data.getInt() & INT_MASK;
            short check = data.getShort();
            
            // check the checksum, both the little and the big endian
            // checksums are accepted. For more information see description
            // of this class
            ByteBuffer checkData = createUncheckedByteArray(ttl,reset,ByteOrder.LITTLE_ENDIAN);
            if(generateChecksum(checkData,ByteOrder.LITTLE_ENDIAN) != check &&
               generateChecksum(bigEndianCheckData,ByteOrder.BIG_ENDIAN) != check)
            { 
                throw new DataFormatException("Invalid checksum");
            }
        } catch(BufferUnderflowException e) {
            throw new DataFormatException("Illegal Hirschmann Package Format");
        }
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#getSize()
     */
    public long getSize() {
    	return ENTIRE_SIZE<minSize ? minSize : ENTIRE_SIZE;
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#toByteArray()
     */
    public ByteBuffer toByteArray() 
    {
    	// create the packet without a checksum, both as little and big endian
        ByteBuffer checkData = createUncheckedByteArray((short) DEFAULT_TTL,
        		DEFAULT_RESET, ByteOrder.LITTLE_ENDIAN);
        
        ByteBuffer data = createUncheckedByteArray((short) DEFAULT_TTL,
        		DEFAULT_RESET, ByteOrder.BIG_ENDIAN);
        
        // calculate the checksum for the little endian package
        data.putShort(ENTIRE_SIZE-2,generateChecksum(checkData,ByteOrder.LITTLE_ENDIAN));
        
        return data;
    }
    
    /**
     * Checks if the passed data buffer contains the correct 
     * header for this type of package.
     * This operation starts the check at the ByteBuffer's current position 
     * but does not move the ByteBuffers position at all.
     * 
     * @param data   The ByteBuffer to be checked
     * @return       Return whether data has a correct header or not.
     */
    public static boolean isCorrectHeader(ByteBuffer data) {
        // the package must be at least the headers size
        if(data.remaining() >= HEADER.length) {
            // extract the header and reset the buffer's position
            byte[] head = new byte[HEADER.length];
            data.mark();
            data.get(head);
            data.reset();
            
            // check if the header is correct
            if(Arrays.equals(HEADER,head)) {
                return true;
            }
        }
        return false;
    }
    
    private static short generateChecksum(ByteBuffer data, ByteOrder order) {
        // holds the results of the calculations
        long sum = 0;
        
        // set the endianess we generate the checksum for
        data.order(order);
        
        // Our algorithm is simply using a 32 bit accumulator (sum),
        // we add sequential 16 bit words to it, and at the end, fold
        // back all the carry bits from the top 16 bits into the lower
        // 16 bits
        
        // do the easy accumulation part
        while (data.remaining() > 1) {
            int c = data.getShort() & SHORT_MASK;
            sum += c;
        }
        
        // if buffer can't be split in shorts
        // imagine appending 0x00 to the buffer
        if (data.limit() % 2 != 0) {
            sum += (data.get(data.limit()-1) & BYTE_MASK);
        }
        
        // add hi 16 to low 16
        sum = (sum >> Short.SIZE) + (sum & SHORT_MASK);
        // add carry
        sum += (sum >> Short.SIZE);
        
        // truncate to 16 bits
        return (short)(~sum);
    }
    
    private ByteBuffer createUncheckedByteArray(short ttl, long reset, ByteOrder endian) {
        // create the binary representation of the package without a checksum
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
    
    private static final byte[] HEADER = {
    	                          'H','i','r','s','c','h','m','a','n','n',
                                  ' ','I','P',' ','T','e','s','t','-','M',
                                  'u','l','t','i','c','a','s','t','\0'};
    
    private static final int CHECKSUM_SIZE = Short.SIZE/Byte.SIZE;
    
    private static final int ENTIRE_SIZE = HEADER.length +Short.SIZE  /Byte.SIZE
                                                         +Integer.SIZE/Byte.SIZE
                                                         +Short.SIZE  /Byte.SIZE
                                                         +Byte.SIZE   /Byte.SIZE
                                                         +Integer.SIZE/Byte.SIZE
                                                         +CHECKSUM_SIZE;
    
	private static final short BYTE_MASK = 0xFF;
	private static final int SHORT_MASK = 0xFFFF;
	private static final long INT_MASK = 0xFFFFFFFFL;

	private static final short DEFAULT_TTL = 0xFF;
	private static final short DEFAULT_RESET = 0;
    
    private long minSize;
    private long senderID;
    private long configuredPacketsPerSeconds;
    private long sequenceNumber;
    
    /**
     * @see com.spam.mctool.model.packet.Packet#getSenderId()
     */
    public long getSenderId() {
        return senderID;
    }
    
    /**
     * @exception IllegalArgumentException thrown if senderId is negative or 
     *                                     greater 0xFFFF (max of unsigned short).
     * 
     * @see com.spam.mctool.model.packet.Packet#setSenderId(long)
     */
    public void setSenderId(long senderId) {
        if((senderId & SHORT_MASK) != senderId) {
            throw new IllegalArgumentException();
        }
        this.senderID = senderId;
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#getConfiguredPacketsPerSecond()
     */
    public long getConfiguredPacketsPerSecond() {
        return configuredPacketsPerSeconds;
    }
    
    /**
     * @exception IllegalArgumentException thrown if configuredPacketsPerSeconds is negative or 
     *                                     greater 0xFFFF (max of unsigned short).
     *                                     
     * @see com.spam.mctool.model.packet.Packet#setConfiguredPacketsPerSecond(long)
     */
    public void setConfiguredPacketsPerSecond(long configuredPacketsPerSeconds) {
        if((configuredPacketsPerSeconds & SHORT_MASK) != configuredPacketsPerSeconds){
            throw new IllegalArgumentException();
        }
        this.configuredPacketsPerSeconds = configuredPacketsPerSeconds;
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#getSenderMeasuredPacketRate()
     */
    public long getSenderMeasuredPacketRate() {
        return 0;
    }
    
    /**
     * @exception UnsupportedOperationException senderMeasuredPacketRate is not
     *                                          supported by packet.
     *                                     
     * @see com.spam.mctool.model.packet.Packet#setSenderMeasuredPacketRate(long)
     */
    public void setSenderMeasuredPacketRate(long senderMeasuredPacketRate) 
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#getSequenceNumber()
     */
    public long getSequenceNumber() {
        return sequenceNumber;
    }
    
    /**
     * @exception IllegalArgumentException thrown if sequenceNumber is negative or 
     *                                     greater 0xFFFFFFFF (max of unsigned int).
     *                                     
     * @see com.spam.mctool.model.packet.Packet#setSequenceNumber(long)
     */
    public void setSequenceNumber(long sequenceNumber) {
        if((sequenceNumber & INT_MASK) != sequenceNumber){
            throw new IllegalArgumentException();
        }
        this.sequenceNumber = sequenceNumber;
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#getDispatchTime()
     */
    public long getDispatchTime() {
        return 0;
    }
    
    /**
     * @exception UnsupportedOperationException dispatchTime is not
     *                                          supported by packet.
     *                                     
     * @see com.spam.mctool.model.packet.Packet#setDispatchTime(long)
     */
    public void setDispatchTime(long dispatchTime) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * @exception UnsupportedOperationException payload is not
     *                                          supported by packet.
     *                                     
     * @see com.spam.mctool.model.packet.Packet#setPayload(byte[])
     */
    public void setPayload(byte[] data) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#getPayload()
     */
    public byte[] getPayload() {
        return new byte[0];
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#setMinimumSize(long)
     */
    public void setMinimumSize(long size) {
        minSize = size;
    }
}
