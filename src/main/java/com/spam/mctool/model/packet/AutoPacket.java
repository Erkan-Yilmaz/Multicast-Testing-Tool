package com.spam.mctool.model.packet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.DataFormatException;

/**
 * This class represents a package that chooses it's
 * binary representation (protocol) automatically.
 * Which representation to use is determined by either
 * the bytestream passed to fromByteArray or a
 * default is used.
 * 
 * @author konne
 *
 */
public class AutoPacket implements Packet {
	public AutoPacket() 
	{
		packet = new SpamPacket();
	}
	/**
	 * Determines the type of package depending 
	 * on the data passed to this function.
	 * @param data    The data to be parsed
	 * @throws DataFormatException   thrown if badly formated data is passed
	 */
	public void fromByteArray(ByteBuffer data) throws DataFormatException 
	{
		data.order(ByteOrder.BIG_ENDIAN);
		
	    // check the header
		if(data.limit() >= 6 && data.getShort(0) == 1337 && data.getInt(2) == 0) {
		    packet = new SpamPacket();
		//} else if(false) {
		//	packet = null; //new HirschmannPacket();
		} else {
			throw new DataFormatException("Unsupported package header");
		}
		
		packet.fromByteArray(data);
	}

	/**
	 * @return
	 * @see com.spam.mctool.model.packet.Packet#toByteArray()
	 */
	public ByteBuffer toByteArray() {
		return packet.toByteArray();
	}

	/**
	 * @return
	 * @see com.spam.mctool.model.packet.Packet#getSenderID()
	 */
	public long getSenderId() {
		return packet.getSenderId();
	}

	/**
	 * @param senderID
	 * @see com.spam.mctool.model.packet.Packet#setSenderID(long)
	 */
	public void setSenderId(long senderID) {
		packet.setSenderId(senderID);
	}

	/**
	 * @return
	 * @see com.spam.mctool.model.packet.Packet#getConfiguredPacketsPerSecond()
	 */
	public long getConfiguredPacketsPerSecond() {
		return packet.getConfiguredPacketsPerSecond();
	}

	/**
	 * @param configuredPacketsPerSeconds
	 * @see com.spam.mctool.model.packet.Packet#setConfiguredPacketsPerSecond(long)
	 */
	public void setConfiguredPacketsPerSecond(long configuredPacketsPerSeconds) {
		packet.setConfiguredPacketsPerSecond(configuredPacketsPerSeconds);
	}

	/**
	 * @return
	 * @see com.spam.mctool.model.packet.Packet#getSenderMeasuredPacketRate()
	 */
	public long getSenderMeasuredPacketRate() {
		return packet.getSenderMeasuredPacketRate();
	}

	/**
	 * @param senderMeasuredPacketRate
	 * @see com.spam.mctool.model.packet.Packet#setSenderMeasuredPacketRate(long)
	 */
	public void setSenderMeasuredPacketRate(long senderMeasuredPacketRate) {
		packet.setSenderMeasuredPacketRate(senderMeasuredPacketRate);
	}

	/**
	 * @return
	 * @see com.spam.mctool.model.packet.Packet#getSequenceNumber()
	 */
	public long getSequenceNumber() {
		return packet.getSequenceNumber();
	}

	/**
	 * @param sequenceNumber
	 * @see com.spam.mctool.model.packet.Packet#setSequenceNumber(long)
	 */
	public void setSequenceNumber(long sequenceNumber) {
		packet.setSequenceNumber(sequenceNumber);
	}

	/**
	 * @return
	 * @see com.spam.mctool.model.packet.Packet#getDispatchTime()
	 */
	public long getDispatchTime() {
		return packet.getDispatchTime();
	}

	/**
	 * @param dispatchTime
	 * @see com.spam.mctool.model.packet.Packet#setDispatchTime(long)
	 */
	public void setDispatchTime(long dispatchTime) {
		packet.setDispatchTime(dispatchTime);
	}
	/**
	 * @param data
	 * @see com.spam.mctool.model.packet.Packet#setPayload(byte[])
	 */
	public void setPayload(byte[] data) {
		packet.setPayload(data);
	}
	/**
	 * @param data
	 * @see com.spam.mctool.model.packet.Packet#getPayload()
	 */
	public byte[] getPayload() {
		return packet.getPayload();
	}
	
	private Packet packet;

}
