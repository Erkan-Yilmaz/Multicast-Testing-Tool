package com.spam.mctool.model.packet;

import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;

/**
 * @author konne
 *
 * This class represents a package that chooses it's
 * binary representation (protocol) automatically.
 * Which representation to use is determined by either
 * the ByteBuffer passed to fromByteArray or a default
 * type is used.
 */
public class AutoPacket implements Packet {
    public AutoPacket() 
    {
        packet = new SpamPacket();
    }
    
    /**
     * Determines the type of package depending 
     * on the data passed to this function.
     * @see com.spam.mctool.model.packet.Packet#fromByteArray(ByteBuffer)
     * @param data                   The data to be parsed
     * @throws DataFormatException   Thrown if badly formated data is passed
     */
    final public void fromByteArray(ByteBuffer data) throws DataFormatException 
    {
        if(SpamPacket.isCorrectHeader(data)){
            packet = new SpamPacket();
        } else if (HirschmannPacket.isCorrectHeader(data)) {
            packet = new HirschmannPacket();
        } else {
            throw new DataFormatException("Unsupported package header");
        }
        
        packet.fromByteArray(data);
    }
    
    /**
     * Get the type of Packet represented by this AutoPacket.
     * @return  Type of Packet
     */
    final public Class<? extends Packet> getPacketType() {
        return packet.getClass();
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#toByteArray()
     */
    final public ByteBuffer toByteArray() {
        return packet.toByteArray();
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#getSenderID()
     */
    final public long getSenderId() {
        return packet.getSenderId();
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#setSenderID(long)
     */
    final public void setSenderId(long senderID) {
        packet.setSenderId(senderID);
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#getConfiguredPacketsPerSecond()
     */
    final public long getConfiguredPacketsPerSecond() {
        return packet.getConfiguredPacketsPerSecond();
    }	
    
    /**
     * @see com.spam.mctool.model.packet.Packet#setConfiguredPacketsPerSecond(long)
     */
    final public void setConfiguredPacketsPerSecond(long configuredPacketsPerSeconds) {
        packet.setConfiguredPacketsPerSecond(configuredPacketsPerSeconds);
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#getSenderMeasuredPacketRate()
     */
    final public long getSenderMeasuredPacketRate() {
        return packet.getSenderMeasuredPacketRate();
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#setSenderMeasuredPacketRate(long)
     */
    final public void setSenderMeasuredPacketRate(long senderMeasuredPacketRate) {
        packet.setSenderMeasuredPacketRate(senderMeasuredPacketRate);
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#getSequenceNumber()
     */
    final public long getSequenceNumber() {
        return packet.getSequenceNumber();
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#setSequenceNumber(long)
     */
    final public void setSequenceNumber(long sequenceNumber) {
        packet.setSequenceNumber(sequenceNumber);
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#getDispatchTime()
     */
    final public long getDispatchTime() {
        return packet.getDispatchTime();
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#setDispatchTime(long)
     */
    final public void setDispatchTime(long dispatchTime) {
        packet.setDispatchTime(dispatchTime);
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#setPayload(byte[])
     */
    final public void setPayload(byte[] data) {
        packet.setPayload(data);
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#getPayload()
     */
    final public byte[] getPayload() {
        return packet.getPayload();
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#getSize()
     */
    final public long getSize() {
        return packet.getSize();
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#setMinimumSize()
     */
    final public void setMinimumSize(long size) {
        packet.setMinimumSize(size);
    }
    
    private Packet packet;
}
