package com.spam.mctool.model;

import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.spam.mctool.controller.Controller;
import com.spam.mctool.controller.ErrorEvent;
import com.spam.mctool.controller.ErrorEventManager;

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
	protected static ErrorEventManager eMan = Controller.getController();
	// network
	protected NetworkInterface networkInterface;
	protected InetAddress group;
	protected int port;
	protected MulticastSocket socket;
	protected Class<?> ipMode;
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
			ident = ident.toLowerCase();
			if(ident.equals("lazy")) {
				return LAZY;
			} else if(ident.equals("default")) {
				return DEFAULT;
			} else if(ident.equals("eager")) {
				return EAGER;
			} else {
				System.out.println("Model.MulticastStream.getAnalyzingBehaviour.IllegalIdentifier.text");
				eMan.reportErrorEvent(
					new ErrorEvent(3, "Model.MulticastStream.getAnalyzingBehaviour.IllegalIdentifier.text", "Fallback: default")
				);
				return DEFAULT;
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
		
		private PacketType(String dispName) {
			this.dispName = dispName;
		}
		
		public String getDisplayName() {
			return this.dispName;
		}
		
		public static PacketType getByIdentifier(String ident) {
			ident = ident.toLowerCase();
			if(ident.equals("hmann")) {
				return PacketType.HMANN;
			} else if(ident.equals("spam")) {
				return PacketType.SPAM;
			} else {
				System.out.println("Model.MulticastStream.getPacketType.IllegalIdentifier.text");
				eMan.reportErrorEvent(
					new ErrorEvent(3, "Model.MulticastStream.getPacketType.IllegalIdentifier.text", "Fallback: Spam format")
				);
				return PacketType.SPAM;
			}
		}
	}
	
	/**
	 * Tries to get a MulticastGroup from a IP Address string.
	 * IPv6 addresses are only supported in an easy 8x4byte from, no shorthands.
	 * @param group the address string
	 * @return InetAddress if correct multicast group is found, null if not
	 */
	// TODO DELETE THIS FUCKING SHIT
	public static InetAddress getMulticastGroupByName(String group) {
		if(null == group) {
			return null;
		}
		
		InetAddress add = null;
		Matcher ipv4Matcher = Pattern.compile("([\\d]{1,3})\\.([\\d]{1,3})\\.([\\d]{1,3})\\.([\\d]{1,3})").matcher(group);
		Matcher ipv6Matcher = Pattern.compile("([a-fA-F\\d]{0,4}):([a-fA-F\\d]{0,4}):([a-fA-F\\d]{0,4}):([a-fA-F\\d]{0,4}):([a-fA-F\\d]{0,4}):([a-fA-F\\d]{0,4}):([a-fA-F\\d]{0,4}):([a-fA-F\\d]{0,4})").matcher(group);
		
		matching:
		if(ipv4Matcher.matches()) {
			int[] address = new int[4];
			for(int i=0; i<4; i++) {
				address[i] = Integer.parseInt(ipv4Matcher.group(i+1));
			}
			if((address[0]<224) || (address[0]>239) || (address[1]>255) || (address[2]>255) || (address[3]>255)) {
				break matching;
			}
			try {
				add = InetAddress.getByName(group);
			} catch(UnknownHostException e) {
				break matching;
			}
		} else if(ipv6Matcher.matches()) {
			int[] address = new int[8];
			for(int i=0; i<8; i++) {
				String part = ipv6Matcher.group(i+1);
				address[i] = (part.equals("")) ? 0 : Integer.decode("0x"+part);
			}
			if((address[0]<0xff00) || (address[0]>0xffff)) {
				break matching;
			}
			try {
				add = InetAddress.getByName(group);
			} catch(UnknownHostException e) {
				break matching;
			}
		} else {
			break matching;
		}
		
		return add;
	}
	
	/**
	 * Tries to return a port identified by the given string
	 * @param name the string denoting the port
	 * @return port if correct, null if not
	 */
	// TODO DELETE THIS FUCKING SHIT
	public static Integer getPortByName(String name) {
		if(null == name) {
			return null;
		}
		
		try {
			Integer port = Integer.parseInt(name);
			if((port<0) || (port>65535)) {
				return null;
			} else {
				return port;
			}
		} catch(NumberFormatException nfe) {
			return null;
		}
	}
	
	/**
	 * Tries to get the Network Interface defined by the given IP address or 
	 * falls back to the first found external interface (not loopback and not point to point)
	 * @param address ip address in string representation
	 * @return the chosen network interface or null if none is found
	 */
	// TODO DELETE THIS FUCKING SHIT
	public static NetworkInterface getNetworkInterfaceByAddress(String address) {
		NetworkInterface ninf = null;
		try {
			boolean found = false;
			Enumeration<NetworkInterface> infs = NetworkInterface.getNetworkInterfaces();
			// iterate through interfaces
			outer:while(infs.hasMoreElements()) {
				ninf = infs.nextElement();
				Enumeration<InetAddress> adds = ninf.getInetAddresses();
				// iterate through addresses
				while(adds.hasMoreElements()) {
					InetAddress add = adds.nextElement();
					String addString = add.getHostAddress().replace("/", "");
					if(addString.equals(address)) {
						found = true;
						break outer;
					}
				}
			}
			// fallback if given ip address is not found
			if(!found) {
				infs = NetworkInterface.getNetworkInterfaces();
				while(infs.hasMoreElements()) {
					ninf = infs.nextElement();
					// search for a external interface
					if(!(ninf.isLoopback() || ninf.isPointToPoint())) {
						found = true;
						eMan.reportErrorEvent(
							new ErrorEvent(3, "Model.MulticastStream.getNetworkInterface.IPNotFound", "Fallback: "+ninf.getDisplayName())
						);
						break;
					}
				}
			}
			if(!found) {
				ninf = null;
			}
		} catch(Exception e) {
			eMan.reportErrorEvent(
				new ErrorEvent(5, "Model.MulticastStream.getNetworkInterface.FatalNetworkError", "")
			);
		}
		return ninf;
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
	public boolean setGroup(Object group) {
		if(group instanceof InetAddress) {
			this.ipMode = group.getClass();
			this.group = (InetAddress) group;
			return true;
		} else if(group instanceof String) {
			Matcher ipv4Matcher = Pattern.compile("([\\d]{1,3})\\.([\\d]{1,3})\\.([\\d]{1,3})\\.([\\d]{1,3})").matcher((String) group);
			Matcher ipv6Matcher = Pattern.compile("([a-fA-F\\d]{0,4}):([a-fA-F\\d]{0,4}):([a-fA-F\\d]{0,4}):([a-fA-F\\d]{0,4}):([a-fA-F\\d]{0,4}):([a-fA-F\\d]{0,4}):([a-fA-F\\d]{0,4}):([a-fA-F\\d]{0,4})").matcher((String) group);
			
			matching: // begin trying to match the address
			if(ipv4Matcher.matches()) { // ip4 matching
				int[] address = new int[4];
				for(int i=0; i<4; i++) {
					address[i] = Integer.parseInt(ipv4Matcher.group(i+1));
				}
				if((address[0]<224) || (address[0]>239) || (address[1]>255) || (address[2]>255) || (address[3]>255)) {
					break matching;
				}
				try {
					this.group = InetAddress.getByName((String) group);
				} catch(UnknownHostException e) {
					break matching;
				}
				return true;
			} else if(ipv6Matcher.matches()) { // ip6 matching
				int[] address = new int[8];
				for(int i=0; i<8; i++) {
					String part = ipv6Matcher.group(i+1);
					address[i] = (part.equals("")) ? 0 : Integer.decode("0x"+part);
				}
				if((address[0]<0xff00) || (address[0]>0xffff)) {
					break matching;
				}
				try {
					this.group = InetAddress.getByName((String) group);
				} catch(UnknownHostException e) {
					break matching;
				}
				return true;
			} else {
				break matching;
			}
			return false;
		} else {
			return false;
		}
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
	public boolean setPort(Object port) {
		this.deactivate();
		if(port instanceof Integer) {
			this.port = (Integer) port;
			this.activate();
			return true;
		} else if(port instanceof String) {
			String name = (String) port;
			try {
				Integer port2 = Integer.parseInt(name);
				if((port2<0) || (port2>65535)) {
					return false;
				} else {
					this.port = port2;
					this.activate();
					return true;
				}
			} catch(NumberFormatException nfe) {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * @return the network interface the socket is opened on
	 */
	public NetworkInterface getNetworkInterface() {
		return networkInterface;
	}

	/**
	 * @param networkInterface may be IP address string or network interface object
	 * Falls back to first network interface which is not loopback nor point2point if
	 * IP address can not be found
	 */
	public boolean setNetworkInterface(Object networkInterface) {
		if(networkInterface instanceof NetworkInterface) {
			this.networkInterface = (NetworkInterface) networkInterface;
			return true;
		} else if(networkInterface instanceof String) {
			String address = (String) networkInterface;
			NetworkInterface ninf = null;
			try {
				boolean found = false;
				Enumeration<NetworkInterface> infs = NetworkInterface.getNetworkInterfaces();
				// iterate through interfaces
				outer:while(infs.hasMoreElements()) {
					ninf = infs.nextElement();
					Enumeration<InetAddress> adds = ninf.getInetAddresses();
					// iterate through addresses
					while(adds.hasMoreElements()) {
						InetAddress add = adds.nextElement();
						String addString = add.getHostAddress().replace("/", "");
						if(addString.equals(address)) {
							found = true;
							break outer;
						}
					}
				}
				// fallback if given ip address is not found
				if(!found) {
					infs = NetworkInterface.getNetworkInterfaces();
					while(infs.hasMoreElements()) {
						ninf = infs.nextElement();
						// search for a external interface
						if(!(ninf.isLoopback() || ninf.isPointToPoint())) {
							found = true;
							eMan.reportErrorEvent(
								new ErrorEvent(3, "Model.MulticastStream.getNetworkInterface.IPNotFound", "Fallback: "+ninf.getDisplayName())
							);
							break;
						}
					}
				}
				if(!found) {
					ninf = null;
					return false;
				} else {
					this.networkInterface = ninf;
					return true;
				}
			} catch(Exception e) {
				eMan.reportErrorEvent(
					new ErrorEvent(5, "Model.MulticastStream.getNetworkInterface.FatalNetworkError", "")
				);
				return false;
			}
		} else {
			eMan.reportErrorEvent(
				new ErrorEvent(5, "Model.MulticastStream.setNetworkInterface.WrongArgument", "")
			);
			return false;
		}
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
	public boolean setAnalyzingBehaviour(Object analyzingBehaviour) {
		if(analyzingBehaviour instanceof AnalyzingBehaviour) {
			this.analyzingBehaviour = (AnalyzingBehaviour) analyzingBehaviour;
			return true;
		} else if(analyzingBehaviour instanceof String) {
			this.analyzingBehaviour = AnalyzingBehaviour.getByIdentifier(
				(String) analyzingBehaviour
			);
			return true;
		} else {
			eMan.reportErrorEvent(
				new ErrorEvent(5, "Model.MulticastStream.setAnalyzingBehaviour.WrongArgument", "")
			);
			return false;
		}
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