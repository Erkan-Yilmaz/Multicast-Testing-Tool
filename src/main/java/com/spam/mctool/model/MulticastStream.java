package com.spam.mctool.model;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Super class of sending and receiving multicast streams
 * @author Jeffrey Jedele
 *
 */
public abstract class MulticastStream implements Runnable {
	
	public static enum State { ACTIVE, INACTIVE }
	protected State state = State.INACTIVE;
	protected InetAddress group;
	
	public static enum AnalyzingBehaviour {
		LAZY(4), DEFAULT(2), EAGER(1);
		private int div;
	
		AnalyzingBehaviour(int div) {
			this.div = div;
		}
	
		public int getDiv() {
			return div;
		}
	}
	protected AnalyzingBehaviour analyzingBehaviour;
	
	protected int port;
	
	protected int measuredPacketRate;
	
	protected int packetSize;
	
	protected byte[] data;
	
	protected int senderConfiguredPacketRate;
	
	protected long sentPacketCount = 0;
	
	protected NetworkInterface networkInterface;
	
	protected MulticastSocket socket;

	protected volatile boolean jobInterrupted;
	
	protected ScheduledThreadPoolExecutor stpe;
	protected ScheduledFuture sf, asf;
	
	/**
	 * activates the multicast stream
	 */
	public abstract void activate();
	
	/**
	 * activates the multicast stream
	 */
	public abstract void deactivate();

	public InetAddress getGroup() {
		return group;
	}

	public void setGroup(InetAddress group) {
		this.group = group;
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getMeasuredPacketRate() {
		return measuredPacketRate;
	}

	public void setMeasuredPacketRate(int measuredPacketRate) {
		this.measuredPacketRate = measuredPacketRate;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public NetworkInterface getNetworkInterface() {
		return networkInterface;
	}

	public void setNetworkInterface(NetworkInterface networkInterface) {
		this.networkInterface = networkInterface;
	}

	public State getState() {
		return state;
	}

	public int getSenderConfiguredPacketRate() {
		return senderConfiguredPacketRate;
	}

	public long getSentPacketCount() {
		return sentPacketCount;
	}
	public int getPacketSize() {
		return packetSize;
	}
	public void setPacketSize(int packetSize) {
		this.packetSize = packetSize;
	}

}