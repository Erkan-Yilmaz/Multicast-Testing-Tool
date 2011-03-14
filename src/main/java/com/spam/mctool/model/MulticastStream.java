package com.spam.mctool.model;

import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Super class of sending and receiving multicast streams
 * @author Jeffrey Jedele
 *
 */
public abstract class MulticastStream implements Runnable {
	
	// internals
	protected State state = State.INACTIVE;
	protected AnalyzingBehaviour analyzingBehaviour;
	protected ScheduledThreadPoolExecutor stpe;
	protected ScheduledFuture<? extends Object> sf, asf;
	// network
	protected NetworkInterface networkInterface;
	protected InetAddress group;
	protected int port;
	protected MulticastSocket socket;
	// statistics
	protected int statsInterval;

	/**
	 * activates the multicast stream
	 */
	public abstract void activate();
	
	/**
	 * activates the multicast stream
	 */
	public abstract void deactivate();
	
	/**
	 * Represents if a stream is active or not
	 */
	public static enum State { ACTIVE, INACTIVE }
	
	/**
	 * Determines the intervals and testing amounts for statistics generation
	 */
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
	
	/**
	 * Represents the packet type of this stream.
	 */
	public static enum PacketType {
		SPAM("Spam Packet Format"),
		HMANN("Hirschmann Packet Format");
		
		private String dispName;
		
		PacketType(String dispName) {
			this.dispName = dispName;
		}
		
		public String getDisplayName() {
			return this.dispName;
		}
		
		public static PacketType getByIdentifier(String ident) {
			if(ident.equals("hmann")) {
				return PacketType.HMANN;
			} else {
				return PacketType.SPAM;
			}
		}
	}

	/**
	 * @return IP multicast group
	 */
	public InetAddress getGroup() {
		return group;
	}

	/**
	 * @param group IP multicast group to set
	 */
	public void setGroup(InetAddress group) {
		this.group = group;
	}
	
	/**
	 * @return port the socket is opened on
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the socket is opened on
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the network interface the socket is opened on
	 */
	public NetworkInterface getNetworkInterface() {
		return networkInterface;
	}

	/**
	 * @param networkInterface network interface the socket is opened on
	 */
	public void setNetworkInterface(NetworkInterface networkInterface) {
		this.networkInterface = networkInterface;
	}

	/**
	 * @return the state the receiver is in
	 */
	public State getState() {
		return state;
	}
	
	/**
	 * @return if the stream is active
	 */
	public boolean isActive() {
		return (state==State.ACTIVE);
	}

	/**
	 * @return the analyzing behaviour of this stream
	 */
	public AnalyzingBehaviour getAnalyzingBehaviour() {
		return analyzingBehaviour;
	}

	/**
	 * @param analyzingBehaviour the analyzing behaviour of this stream
	 */
	public void setAnalyzingBehaviour(AnalyzingBehaviour analyzingBehaviour) {
		this.analyzingBehaviour = analyzingBehaviour;
	}

	/**
	 * @return base interval between two analyzing cycles in ms
	 */
	public int getStatsInterval() {
		return statsInterval;
	}

	/**
	 * @param statsInterval base interval between two analyzing cycles in ms
	 */
	public void setStatsInterval(int statsInterval) {
		this.statsInterval = statsInterval;
	}

}