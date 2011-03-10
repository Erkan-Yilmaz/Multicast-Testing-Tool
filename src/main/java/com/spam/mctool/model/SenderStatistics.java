package com.spam.mctool.model;

public class SenderStatistics {
	private long sentPackets;
	private long minPPS;
	private long avgPPS;
	private long maxPPS;
	
	public long getSentPackets() {
		return sentPackets;
	}
	public long getMinPPS() {
		return minPPS;
	}
	public long getAvgPPS() {
		return avgPPS;
	}
	public long getMaxPPS() {
		return maxPPS;
	}
	void setSentPackets(long sentPackets) {
		this.sentPackets = sentPackets;
	}
	void setMinPPS(long minPPS) {
		this.minPPS = minPPS;
	}
	void setAvgPPS(long avgPPS) {
		this.avgPPS = avgPPS;
	}
	void setMaxPPS(long maxPPS) {
		this.maxPPS = maxPPS;
	}
	
}
