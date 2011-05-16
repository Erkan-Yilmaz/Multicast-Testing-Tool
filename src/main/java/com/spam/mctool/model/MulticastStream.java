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

		private AnalyzingBehaviour(int div, String ident, double inc, double init) {
			this.div = div;
			this.ident = ident;
			this.inc = inc;
			this.init = init;
		}

		/**
		 * If this returns i, statistics are calculated new every ith second.
		 * @return i
		 */
		public int getDiv() {
			return div;
		}
		
		/**
		 * Returns a string value identifier of the AnalyzingBehaviour.
		 * @return identifier, may be "lazy", "default" or "eager"
		 */
		public String getIdentifier() {
			return ident;
		}
		
		/**
		 * If this return i, only every ith value of the collected values will
		 * be regarded in statistics calculation. The current measured packet rate
		 * will be regarded to make the value sample big enough.
		 * @param pps current measured packet rate.
		 * @return i
		 */
		public int getDynamicStatsStepWidth(long pps) {
			return (int)Math.ceil(pps*inc+init);
		}
		
		/**
		 * Tries to get a AnalyzingBehaviour by the identifier or falls back to
		 * DEFAULT.
		 * @param identifier
		 * @return parsed A.B.
		 */
		public static AnalyzingBehaviour getByIdentifier(String ident) {
			ident = ident.toLowerCase();
			if(ident.equals("lazy")) {
				return LAZY;
			} else if(ident.equals("default")) {
				return DEFAULT;
			} else if(ident.equals("eager")) {
				return EAGER;
			} else {
				eMan.reportErrorEvent(
					new ErrorEvent(3, "Model.Multicast.AnalyzingBehaviour.Wrong.text", "Fallback: default")
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
		
		/**
		 * Returns a human readable name of the Packet Type.
		 * @return name
		 */
		public String getDisplayName() {
			return this.dispName;
		}
		
		/**
		 * Tries to parse a PacketType by a passed identifier or falls back
		 * to SPAM
		 * @param identifier
		 * @return parsed P.T.
		 */
		public static PacketType getByIdentifier(String ident) {
			ident = ident.toLowerCase();
			if(ident.equals("hmann")) {
				return PacketType.HMANN;
			} else if(ident.equals("spam")) {
				return PacketType.SPAM;
			} else {
				eMan.reportErrorEvent(
					new ErrorEvent(3, "Model.Multicast.PacketType.Wrong.text", "Fallback: Spam format")
				);
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
	 * Sets multicast group without checks if parameter is an InetAddress.
	 * Tries to parse an InetAddress with range checks if parameter is string.
	 * Refer to IP multicast description for valid IP ranges.
	 * @param InetAddress or string representation
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
					if(address[i]<0) {
						break matching;
					}
				}
				if((address[0]<224) || (address[0]>239) || (address[1]>255) || (address[2]>255) || (address[3])==0 || (address[3]>=255)) {
					break matching;
					// no valid multicast address
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
				if((address[0]<0xff00) || (address[0]>0xffff) || (address[7]>0xfffe) || (address[7]==0x0000)) {
					break matching;
					// no valid multicast address
				}
				try {
					// crazy to-way zero replace
					group = ((String) group).replaceAll("::", ":0:");
					group = ((String) group).replaceAll("::", ":0:");
					this.group = InetAddress.getByName((String) group);
				} catch(UnknownHostException e) {
					break matching;
				}
				return true;
			} else {
				// no ip format
				break matching;
			}
			// only reached if something in ip matching block goes wrong
			eMan.reportErrorEvent(
				new ErrorEvent(4, "Model.Multicast.Group.Wrong.text", group.toString())
			);
			return false;
		} else {
			// illegal argument
			eMan.reportErrorEvent(
				new ErrorEvent(5, "Model.Multicast.Fatal.text", "Error Code: MulticastStream.setGroup.WrongArgument")
			);
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
	 * Sets the port.
	 * No checks if parameter is Integer.
	 * Tries to parse a port with range checks if parameter is string.
	 * @param port
	 */
	public boolean setPort(Object port) {
		if(port instanceof Integer) {
			this.port = (Integer) port;
			return true;
		} else if(port instanceof String) {
			String name = (String) port;
			try {
				Integer port2 = Integer.parseInt(name);
				if((port2<0) || (port2>65535)) {
					eMan.reportErrorEvent(
						new ErrorEvent(4, "Model.Multicast.Port.Wrong.text", name)
					);
					return false;
				} else {
					this.port = port2;
					return true;
				}
			} catch(NumberFormatException nfe) {
				eMan.reportErrorEvent(
					new ErrorEvent(4, "Model.Multicast.Port.Wrong.text", name)
				);
				return false;
			}
		} else {
			eMan.reportErrorEvent(
				new ErrorEvent(5, "Model.Multicast.Fatal.text", "Error Code: MulticastStream.setPort.WrongArgument")
			);
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
	 * Sets a network interface for the datastream.
	 * No checks if parameter is a NetworkInterface.
	 * Tries to parse from IP address if parameter is a string.
	 * Falls back to first network interface which is not loopback nor point2point if
	 * interface with given IP address can not be found.
	 * @param networkInterface
	 */
	public boolean setNetworkInterface(Object networkInterface) {
		if(networkInterface instanceof NetworkInterface) {
			// set the interdace directly if given
			this.networkInterface = (NetworkInterface) networkInterface;
			return true;
		} else if(networkInterface instanceof String) {
			// try to get interface from ip
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
								new ErrorEvent(3, "Model.Multicast.Interface.NotFound.text", "Fallback: "+ninf.getDisplayName())
							);
							break;
						}
					}
				}
				if(!found) {
					// finally here if nothing was found
					ninf = null;
					return false;
				} else {
					// finally here if a solution was found
					this.networkInterface = ninf;
					return true;
				}
			} catch(Exception e) {
				eMan.reportErrorEvent(
					new ErrorEvent(5, "Model.Multicast.FatalNetworkError.text", "Error Code: MulticastStream.setNetworkInterface.InterfaceException")
				);
				return false;
			}
		} else {
			eMan.reportErrorEvent(
				new ErrorEvent(5, "Model.Multicast.Fatal.text", "Error Code: MulticastStream.setNetworkInterface.WrongArgument")
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