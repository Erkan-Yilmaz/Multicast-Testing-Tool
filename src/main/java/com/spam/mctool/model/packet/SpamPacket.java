package com.spam.mctool.model.packet;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.DataFormatException;

/**
 * @author konne
 * 
 * Represents a packet of the binary Spam format as specified in the SAS.
 */
final public class SpamPacket implements Packet {
	private final static int SHORT_MASK = 0xFFFF;
	private final static long INT_MASK = 0xFFFFFFFFL;
	
    /**
     * @see com.spam.mctool.model.packet.Packet#fromByteArray(ByteBuffer)
     */
    public void fromByteArray(ByteBuffer data) throws DataFormatException 
    {
        // set default values
        minSize = 0;
        senderID = 0;
        configuredPacketsPerSeconds = 0;
        senderMeasuredPacketRate = 0;
        sequenceNumber = 0;
        dispatchTime = 0;
        payload = new byte[0];
        
        data.order(ByteOrder.BIG_ENDIAN);
        
        int lastId = 0;
        
        try {
            // process all tlv tuples
            outer: while(data.hasRemaining()) {
                // read the header
                int id = data.getShort() & SHORT_MASK;
                long length = data.getInt() & INT_MASK;
                
                // find the correct tuple
                // this over-complicated loop is simply iterating over all
                // available tuples in TlvTuple. The only reason it is not 
                // a for-each loop is that this is faster if the tuples are 
                // coming in in order (which they should be if generated
                // by our tool)
                int i = lastId;
                do {
                    TlvTuple tuple = TlvTuple.values()[i];
                    if(id == tuple.getId()) {
                        if(tuple.hasFixedSize() && length != tuple.calcSize(this)) {
                            throw new DataFormatException("Tuple with Id "+id+
                                    " has illegal data size");
                        }
                        
                        // parse the tuple
                        tuple.readTupleData(data, length, this);
                        continue outer;
                    }
                    
                    i = ++i % TlvTuple.values().length;
                } while(i != lastId);
                lastId = i;
                
                // no tuple matched
                // advance by the size specified in the tlv header
                long pos = (long)data.position() + length;
                // if the position overflows 2GB
                if((int)pos != pos){
                    // we know for a fact that a buffer class does not support
                    // being larger 2GB thus if the position overflows, we know
                    // we are out of the range of the buffer
                    throw new DataFormatException("Illegal TLV Tuple length");
                }
                
                data.position((int)pos);
            }
        } catch(BufferUnderflowException e) {
        	DataFormatException p = new DataFormatException("Illegal TLV Tuple format");
        	p.initCause(e);
            throw p;
        }
        catch(IllegalArgumentException e) {
            // thrown if no tuple matches and the length
            // was too big and lead to buffer underflow
            throw new DataFormatException("Illegal TLV Tuple length");
        }
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#toByteArray()
     */
    public ByteBuffer toByteArray() 
    {
        long size = getSize();
        
        // even though, not illegal according to specification,
        // a packet may not be larger 2GB
        if(size > Integer.MAX_VALUE) {
            throw new UnsupportedOperationException(
                    "Generating packets larger 2GB is not supported");
        }
        
        ByteBuffer buffer = ByteBuffer.allocate((int)size);
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        // needed in order to set the position to the beginning again
        buffer.mark();
        
        // write the tuples
        for(TlvTuple tuple : TlvTuple.values()){
            // writes the header, rest needs to be 
            // written by Tuple Implementation
            buffer.putShort((short)tuple.getId());
            buffer.putInt((int)tuple.calcSize(this));
            // which is done here
            tuple.writeTupleData(buffer, this);
        }
        
        buffer.reset();
        
        return buffer;
    }
    
    
    /**
     * Checks if the passed data buffer contains the correct 
     * header for this type of package.
     * 
     * @param data   The ByteBuffer to be checked
     * @return       Return whether data has a correct header or not.
     */
    public static boolean isCorrectHeader(ByteBuffer data) {
        data.order(ByteOrder.BIG_ENDIAN);
        int pos = data.position();
        
        return (
           data.remaining() >= TLV_HEADER_SIZE &&
           data.getShort(pos) == TlvTuple.HEADER.getId() &&
           data.getInt(pos + Short.SIZE/Byte.SIZE) == 
           TlvTuple.HEADER.calcSize(null)); // size is fixed, thus no package 
                                            // needed and null is ok
    }
    
    private static final int TLV_HEADER_SIZE = Integer.SIZE/Byte.SIZE + 
                                               Short.SIZE/Byte.SIZE;
    
    long getMinimumSize() {
        return minSize;
    }
    
    long getSizeWithoutPadding() {
        long size = TLV_HEADER_SIZE; // Size of the padding header is included
        for(TlvTuple tuple : TlvTuple.values()){
            if(tuple != TlvTuple.PADDING){
                size += TLV_HEADER_SIZE + tuple.calcSize(this);
            }
        }
        
        return size;
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#getSenderId()
     */
    public long getSenderId() {
        return senderID;
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#setSenderId(long)
     */
    public void setSenderId(long senderID) {
        if((senderID & INT_MASK) != senderID){
            throw new IllegalArgumentException();
        }
        this.senderID = senderID;
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#getConfiguredPacketsPerSecond()
     */
    public long getConfiguredPacketsPerSecond() {
        return configuredPacketsPerSeconds;
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#setConfiguredPacketsPerSecond(long)
     */
    public void setConfiguredPacketsPerSecond(long configuredPacketsPerSeconds) {
        if((configuredPacketsPerSeconds & INT_MASK) != configuredPacketsPerSeconds){
            throw new IllegalArgumentException();
        }
    
        this.configuredPacketsPerSeconds = configuredPacketsPerSeconds;
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#getSenderMeasuredPacketRate()
     */
    public long getSenderMeasuredPacketRate() {
        return senderMeasuredPacketRate;
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#setSenderMeasuredPacketRate(long)
     */
    public void setSenderMeasuredPacketRate(long senderMeasuredPacketRate) {
        if((senderMeasuredPacketRate & INT_MASK) != senderMeasuredPacketRate){
            throw new IllegalArgumentException();
        }
        this.senderMeasuredPacketRate = senderMeasuredPacketRate;
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#getSequenceNumber()
     */
    public long getSequenceNumber() {
        return sequenceNumber;
    }
    
    /**
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
        return dispatchTime;
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#setDispatchTime(long)
     */
    public void setDispatchTime(long dispatchTime) {
        this.dispatchTime = dispatchTime;
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#setPayload(byte[] date)
     */
    public void setPayload(byte[] data) {
        payload = data.clone();
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#getPayload()
     */
    public byte[] getPayload() {
        return payload.clone();
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#getSize()
     */
    public long getSize() {
        return getSizeWithoutPadding() + TlvTuple.PADDING.calcSize(this);
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#setMinimumSize(long)
     */
    public void setMinimumSize(long size) {
        minSize = size;
    }
    
    private long minSize;
    private long senderID;
    private long configuredPacketsPerSeconds;
    private long senderMeasuredPacketRate;
    private long sequenceNumber;
    private long dispatchTime;
    byte[] payload = new byte[0];
}
