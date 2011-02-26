package com.spam.mctool.model.packet;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.DataFormatException;

class TLVParser {
    void parse(ByteBuffer data, Packet packet) throws DataFormatException {
        this.data = data;
        this.packet = packet;
        
        data.order(ByteOrder.BIG_ENDIAN);
        
        while(data.hasRemaining()) {
        	try {
        		int id = data.getShort() & 0xffff;
        		long length = data.getInt() & 0xffffffffL;
            	nextTupel(id,length);
        	} catch(BufferUnderflowException e) {
        		throw new DataFormatException("Illegal TLV Tupel format");
        	}
        }
    }
    
    protected ByteBuffer data;
    protected Packet packet;
    
    public void nextTupel(int id, long length) throws DataFormatException {
        switch(id) {
        case 1337:
        	if(length != 0)
                throw new DataFormatException("Illegal Header Field");  
        	break;
        case 1:
            if(length != 4)
                throw new DataFormatException("Illegal Sender Id Field");
            packet.setSenderId(data.getInt() & 0xffffffffL);
            break;
        case 2:
            if(length != 4)
                throw new DataFormatException("Illegal Configured Packets Per Second Filed");
            packet.setConfiguredPacketsPerSecond(data.getInt() & 0xffffffffL);
            break;
        case 3:
            if(length != 4)
                throw new DataFormatException("Illegal Sender Measured Packet Rate Field");
            packet.setSenderMeasuredPacketRate(data.getInt() & 0xffffffffL);
            break;
        case 4:
            if(length != 4)
                throw new DataFormatException("Illegal Sequence Number Field");
            packet.setSequenceNumber(data.getInt() & 0xffffffffL);
            break;
        case 5:
            if(length != 8)
                throw new DataFormatException("Illegal Dispatch Time Field");
            packet.setDispatchTime(data.getLong());
            break;
        case 6:
        	if(length > Integer.MAX_VALUE) {
        		// well java only supports arrays up to 2GB, if the payload is larger 2GB
        		// we have a problem. Luckily it is not really our problem, because the 
        		// ByteBuffer that was passed into this function does not support data
        		// larger 2GB either. So if the length is larger 2GB we can throw an
        		// BufferUnderflowException.
        		throw new BufferUnderflowException();
        	}
        	
        	byte[] load = new byte[(int)length];
            try {
            	data.get(load);
            } catch(BufferUnderflowException e) {
            	// this is thrown if the get operation tries to
            	// read data outside of the buffers limits.
            	throw new DataFormatException("Payload size does not match real payload's size");
            }
            packet.setPayload(load); 
            break;
        default:
        	try {
        		data.position((int)(data.position()+length));
        	} catch(IllegalArgumentException e) {
        		// the position is too big and leads to buffer underflow
        		throw new BufferUnderflowException(); 
        	}
        }
    }
}

/**
 * @author konne
 *
 */
public class SpamPacket implements Packet {
	/**
	 * @param data	the data to be set
	 * @throws DataFormatException thrown if badly formated data is passed
	 */
	public void fromByteArray(ByteBuffer data) throws DataFormatException 
	{
		TLVParser parser = new TLVParser();
		parser.parse(data, this);
	}
	
	private static void putTlv(ByteBuffer data, int id, long size) {
		data.putShort((short)id);
		data.putInt((int)size);
	}
	
	/**
	 * @return	the byte representation of this packet
	 */
	public ByteBuffer toByteArray() 
	{
		// tlv headers + fixed size fields + payload size
		long size = 7*6 + 0+4+4+4+4+8+ getPayload().length;
		if(size > Integer.MAX_VALUE) 
			throw new UnsupportedOperationException("Generating packets larger 2GB is not supported");
		ByteBuffer buffer = ByteBuffer.allocate((int)size);
		buffer.order(ByteOrder.BIG_ENDIAN);
		
		buffer.mark();
		
		putTlv(buffer,1337,0);	// header
		putTlv(buffer,1,4);
		buffer.putInt((int)getSenderId());
		putTlv(buffer,2,4);
		buffer.putInt((int)getConfiguredPacketsPerSecond());
		putTlv(buffer,3,4);
		buffer.putInt((int)getSenderMeasuredPacketRate());
		putTlv(buffer,4,4);
		buffer.putInt((int)getSequenceNumber());
		putTlv(buffer,5,8);
		buffer.putLong(getDispatchTime());
		putTlv(buffer,6,getPayload().length);
		buffer.put(getPayload());
		
		buffer.reset();
		
		return buffer;
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
		return senderMeasuredPacketRate;
	}
	/**
	 * @param senderMeasuredPacketRate the senderMeasuredPacketRate to set
	 */
	public void setSenderMeasuredPacketRate(long senderMeasuredPacketRate) {
		this.senderMeasuredPacketRate = senderMeasuredPacketRate;
	}
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
		return dispatchTime;
	}
	/**
	 * @param dispatchTime the dispatchTime to set
	 */
	public void setDispatchTime(long dispatchTime) {
		this.dispatchTime = dispatchTime;
	}
	
	/**
	 * @param data  The payload's data
	 */
	public void setPayload(byte[] data) {
	    payload = data;
	}
	
	/**
	 * @return      Returns the payload's data
	 */
	public byte[] getPayload() {
	    return payload;
	}
	
	private long senderID;
	private long configuredPacketsPerSeconds;
	private long senderMeasuredPacketRate;
	private long sequenceNumber;
	private long dispatchTime;
	private byte[] payload;
}
