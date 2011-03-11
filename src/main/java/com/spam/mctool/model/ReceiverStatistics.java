package com.spam.mctool.model;

/**
 * Contains information a receiver has gathered about a specific sender ID in his group
 * @author Jeffrey Jedele
 *
 */
public class ReceiverStatistics {
	private long senderId;
	private long sentPackets;
	private long receivedPackets;
	private long lostPackets;
	private long maxDelay;
	private long configuredPPS;
	private long senderMeasuredAvgPPS;
	private long receiverMeasuredMinPPS;
	private long receiverMeasuredAvgPPS;
	private long receiverMeasuredMaxPPS;
	private long minTraversal;
	private long avgTraversal;
	private long maxTraversal;
	private int packetSize;
	private String payload = "";
	
	public long getSenderId() {
		return senderId;
	}
	public long getSentPackets() {
		return sentPackets;
	}
	public long getReceivedPackets() {
		return receivedPackets;
	}
	public long getLostPackets() {
		return lostPackets;
	}
	public long getMaxDelay() {
		return maxDelay;
	}
	public long getConfiguredPPS() {
		return configuredPPS;
	}
	public long getSenderMeasuredAvgPPS() {
		return senderMeasuredAvgPPS;
	}
	public long getReceiverMeasuredMinPPS() {
		return receiverMeasuredMinPPS;
	}
	public long getReceiverMeasuredAvgPPS() {
		return receiverMeasuredAvgPPS;
	}
	public long getReceiverMeasuredMaxPPS() {
		return receiverMeasuredMaxPPS;
	}
	public long getMinTraversal() {
		return minTraversal;
	}
	public long getAvgTraversal() {
		return avgTraversal;
	}
	public long getMaxTraversal() {
		return maxTraversal;
	}
	public int getPacketSize() {
		return packetSize;
	}
	public String getPayload() {
		return payload;
	}
	void setSenderId(long senderId) {
		this.senderId = senderId;
	}
	protected void setSentPackets(long sentPackets) {
		this.sentPackets = sentPackets;
	}
	void setReceivedPackets(long receivedPackets) {
		this.receivedPackets = receivedPackets;
	}
	void setLostPackets(long lostPackets) {
		this.lostPackets = lostPackets;
	}
	void setMaxDelay(long maxDelay) {
		this.maxDelay = maxDelay;
	}
	void setConfiguredPPS(long configuredPPS) {
		this.configuredPPS = configuredPPS;
	}
	void setSenderMeasuredAvgPPS(long senderMeasuredAvgPPS) {
		this.senderMeasuredAvgPPS = senderMeasuredAvgPPS;
	}
	void setReceiverMeasuredMinPPS(long receiverMeasuredMinPPS) {
		this.receiverMeasuredMinPPS = receiverMeasuredMinPPS;
	}
	void setReceiverMeasuredAvgPPS(long receiverMeasuredAvgPPS) {
		this.receiverMeasuredAvgPPS = receiverMeasuredAvgPPS;
	}
	void setReceiverMeasuredMaxPPS(long receiverMeasuredMaxPPS) {
		this.receiverMeasuredMaxPPS = receiverMeasuredMaxPPS;
	}
	void setMinTraversal(long minTraversal) {
		this.minTraversal = minTraversal;
	}
	void setAvgTraversal(long avgTraversal) {
		this.avgTraversal = avgTraversal;
	}
	void setMaxTraversal(long maxTraversal) {
		this.maxTraversal = maxTraversal;
	}
	void setPacketSize(int packetSize) {
		this.packetSize = packetSize;
	}
	void setPayload(String payload) {
		this.payload = payload;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("--- Receiver Stats for: "+senderId+"---\n");
		sb.append("Div (R.P.|L.P.|M.Del): "+receivedPackets+"|"+lostPackets+"|"+maxDelay+"\n");
		sb.append("Sender (C.PPS|M.PPS|S.P|P.S): "+configuredPPS+"|"+senderMeasuredAvgPPS+"|"+sentPackets+"|"+packetSize+"\n");
		sb.append("R.PPS (MIN|AVG|MAX): "+receiverMeasuredMinPPS+"|"+receiverMeasuredAvgPPS+"|"+receiverMeasuredMaxPPS+"\n");
		sb.append("R.Trav (MIN|AVG|MAX): "+minTraversal+"|"+avgTraversal+"|"+maxTraversal+"\n");
		sb.append("Payload: "+payload+"\n");
		sb.append("-----------------------------------------------\n");
		return sb.toString();
	}
	
}
