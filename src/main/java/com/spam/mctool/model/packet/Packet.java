package com.spam.mctool.model.packet;

import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;

/**
 * @author konne
 * packet
 */
public interface Packet {
	/**
	 * @param data	the data to be set
	 * @throws DataFormatException    thrown if badly formated data is passed
	 */
	public void fromByteArray(ByteBuffer data) throws DataFormatException ;
	/**
	 * @return	the byte representation of this packet
	 */
	public ByteBuffer toByteArray();
	/**
	 * @return the senderID
	 */
	public long getSenderId();
	/**
	 * @param senderID the senderID to set
	 */
	public void setSenderId(long senderId);
	/**
	 * @return the configuredPacketsPerSeconds
	 */
	public long getConfiguredPacketsPerSecond();
	/**
	 * @param configuredPacketsPerSeconds the configuredPacketsPerSeconds to set
	 */
	public void setConfiguredPacketsPerSecond(long configuredPacketsPerSeconds);
	/**
	 * @return the senderMeasuredPacketRate
	 */
	public long getSenderMeasuredPacketRate();
	/**
	 * @param senderMeasuredPacketRate the senderMeasuredPacketRate to set
	 */
	public void setSenderMeasuredPacketRate(long senderMeasuredPacketRate);
	/**
	 * @return the sequenceNumber
	 */
	public long getSequenceNumber();
	/**
	 * @param sequenceNumber the sequenceNumber to set
	 */
	public void setSequenceNumber(long sequenceNumber);
	/**
	 * @return the dispatchTime
	 */
	public long getDispatchTime();
	/**
	 * @param dispatchTime the dispatchTime to set
	 */
	public void setDispatchTime(long dispatchTime);
	/**
	 * @param data  The payload's data
	 */
	public void setPayload(byte[] data);
	/**
	 * @return      Returns the payload's data
	 */
	public byte[] getPayload();
	/**
	 * @return      Returns the packet's size
	 */
	public long getSize();
	/**
	 * Set the size of the package.
	 * Package may be bigger, e.g. if size is set to 1
	 * because we need to send the header and other stuff
	 * 
	 * @param size  The packets minimum size
	 */
	public void setMinimumSize(long size);
}
