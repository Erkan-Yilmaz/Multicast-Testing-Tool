package com.spam.mctool.model.packet;

import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;

/**
 * @author konne
 * 
 * A Packet represents one self contained piece of information that is send 
 * over the network. The specific implementation of the Package is defined
 * in the class implementing this interface. You may thus get
 * IllegalArgumentExceptions or UnsupportedOperationException exceptions.
 */
public interface Packet {
    /**
     * Creates a Packet from its binary representation.
     * @param  data                   The data to be parsed
     * @throws DataFormatException    Thrown if badly formated data is passed
     */
    void fromByteArray(ByteBuffer data) throws DataFormatException ;
    /**
     * Create the binary representation of this Packer
     * @return    The byte representation of this packet
     */
    ByteBuffer toByteArray();
    /**
     * Returns the senderId.
     * @return    The senderId
     */
    long getSenderId();
    /**
     * 
     * @param senderId   The senderId to set
     */
    void setSenderId(long senderId);
    /**
     * @return the configuredPacketsPerSeconds
     */
    long getConfiguredPacketsPerSecond();
    /**
     * @param configuredPacketsPerSeconds the configuredPacketsPerSeconds to set
     */
    void setConfiguredPacketsPerSecond(long configuredPacketsPerSeconds);
    /**
     * @return the senderMeasuredPacketRate
     */
    long getSenderMeasuredPacketRate();
    /**
     * @param senderMeasuredPacketRate the senderMeasuredPacketRate to set
     */
    void setSenderMeasuredPacketRate(long senderMeasuredPacketRate);
    /**
     * @return the sequenceNumber
     */
    long getSequenceNumber();
    /**
     * @param sequenceNumber the sequenceNumber to set
     */
    void setSequenceNumber(long sequenceNumber);
    /**
     * @return the dispatchTime
     */
    long getDispatchTime();
    /**
     * @param dispatchTime the dispatchTime to set
     */
    void setDispatchTime(long dispatchTime);
    
    /**
     * Set the packet's payload.
     * A payload is arbitrary data that can be attached to the packet.
     * 
     * @param data  The payload's data
     */
    void setPayload(byte[] data);
    
    /**
     * Get the packet's payload.
     * 
     * @return      Returns the payload's payload
     */
    byte[] getPayload();
    
    /**
     * Set the minimum size of the package.
     * The package may be larger than the size specified.
     * For example if the size is set to 1, the package will
     * be larger because of header that need to be send also.
     * 
     * @param size  The packets minimum size
     */
    void setMinimumSize(long size);
    
    /**
     * Get the packet's size.
     * 
     * @return      Returns the packet's size
     */
    long getSize();
}
