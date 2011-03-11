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
		LAZY(4, "lazy", 11.0/111.0, 100.0/111.0),
		DEFAULT(2, "default", 49.0/999.0, 950.0/999.0),
		EAGER(1, "eager", 19.0/999.0, 980.0/999.0);
		
		private int div;
		private String ident;
		private double inc;
		private double init;
	
		AnalyzingBehaviour(int div, String ident, double inc, double init) {
			this.div = div;
			this.ident = ident;
			this.inc = inc;
			this.init = init;
		}
	
		public int getDiv() {
			return div;
		}
		
		public String getIdentifier() {
			return ident;
		}
		
		public int getDynamicStatsStepWidth(long pps) {
			return (int)Math.ceil(pps*inc+init);
		}
		
		public static AnalyzingBehaviour getByIdentifier(String ident) {
			if(ident.equals("lazy")) {
				return LAZY;
			} else if(ident.equals("default")) {
				return DEFAULT;
			} else if(ident.equals("eager")) {
				return EAGER;
			} else {
				throw new IllegalArgumentException();
			}
		}
	}
	protected AnalyzingBehaviour analyzingBehaviour;
	protected int statsInterval;
	
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

	public AnalyzingBehaviour getAnalyzingBehaviour() {
		return analyzingBehaviour;
	}

	public void setAnalyzingBehaviour(AnalyzingBehaviour analyzingBehaviour) {
		this.analyzingBehaviour = analyzingBehaviour;
	}

	public int getStatsInterval() {
		return statsInterval;
	}

	public void setStatsInterval(int statsInterval) {
		this.statsInterval = statsInterval;
	}

}