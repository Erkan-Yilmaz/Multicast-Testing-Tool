package com.spam.mctool.model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.spam.mctool.controller.Controller;
import com.spam.mctool.controller.ErrorEvent;
import com.spam.mctool.controller.ErrorEventManager;
import com.spam.mctool.intermediates.SenderDataChangedEvent;
import com.spam.mctool.model.packet.HirschmannPacket;
import com.spam.mctool.model.packet.Packet;
import com.spam.mctool.model.packet.SpamPacket;

/**
 * This class represents a sender which sends in a multicast group.
 * @author Jeffrey Jedele
 */
public class Sender extends MulticastStream {
	
	// constants
	public static final int MAX_SENDER_ID = 65535;
	public static final long MAX_PACKET_NO = 4294967295L;
	// internals
	private AnalyzeSender analyzer;
	private LinkedSplitQueue<Integer> sentTimes;
	private List<SenderDataChangeListener> senderDataChangeListeners = new ArrayList<SenderDataChangeListener>();
	private long lastSent = 0;
	private long nowSent = 0;
	private long lastSentPacketNo = 0;
	private ErrorEventManager eMan;
	// sender specific
	private long senderId;
	private int ttl;
	private PacketType pType;
	private byte[] data;
	private int packetSize;
	private int senderConfiguredPacketRate;
	// statistics
	private int statsStepWidth;
	private long sentPacketCount = 0;
	private long avgPPS;
	private long minPPS = Integer.MAX_VALUE;
	private long maxPPS = Integer.MIN_VALUE;
	private boolean overloaded = false;
	
	/**
	 * Used by the sender manager to create a new receiver.
	 * @param stpe thread pool this is executed in
	 */
	protected Sender(ScheduledThreadPoolExecutor stpe) {
		this.eMan = Controller.getController();
		this.stpe = stpe;
		this.sentTimes = new LinkedSplitQueue<Integer>();
		this.senderId = (long) (MAX_SENDER_ID*Math.random());
		this.analyzer = new AnalyzeSender();
	}
	
	@Override
	public void activate() {
	    if(state == State.ACTIVE) {
	        return;
	    }
		try {
			// open the network socket and join group
			socket = new MulticastSocket(port);
			socket.setNetworkInterface(networkInterface);
			socket.setTimeToLive(ttl);
			socket.joinGroup(group);
			// schedule sending and analyzing jobs
			long period = (long) (1E3/this.senderConfiguredPacketRate);
			sf = this.stpe.scheduleAtFixedRate(this, 0, period, TimeUnit.MILLISECONDS);
			statsStepWidth = analyzingBehaviour.getDynamicStatsStepWidth(senderConfiguredPacketRate);
			analyzer.counter = analyzingBehaviour.getDiv()-1;
			asf = this.stpe.scheduleWithFixedDelay(analyzer, statsInterval, statsInterval, TimeUnit.MILLISECONDS);
			state = State.ACTIVE;
		} catch (IOException e) {
			eMan.reportErrorEvent(
				new ErrorEvent(5, "Model.Multicast.FatalNetworkError.text", e.getMessage())
			);
		}
	}
	
	@Override
	public void deactivate() {
	    if(state == State.INACTIVE) {
	        return;
	    }
		sf.cancel(true);
		asf.cancel(true);
		this.socket.close();
		this.state = State.INACTIVE;
		// clean up statistics
		analyzer.counter = -1;
		analyzer.run();
	}
	
	/**
	 * This is automatically run by the executing thread pool when the stream is active.
	 */
	public void run() {
		try {
			byte[] p = this.getPacket().toByteArray().array();
			//byte[] p = new byte[]{1,2,3,4};
			DatagramPacket dp = new DatagramPacket(p, p.length, this.getGroup(), this.getPort());
			this.socket.send(dp);
			// no synchronization needed because usage of java concurrency api
			nowSent = System.nanoTime();
			if(sentPacketCount == lastSentPacketNo+1) {
				this.sentTimes.enqueue((int)(nowSent-lastSent));
			}
			lastSent = nowSent;
			lastSentPacketNo = sentPacketCount;
		} catch (Exception e) {
			//eMan.reportErrorEvent(
			//	new ErrorEvent(5, "Model.Sender.run.PacketCouldNoBeSent", "")
			//);
		}
	}
	
	// this is used as packet factory for packets
	private Packet getPacket() {
		Packet p = null;
		try {
			if(pType == PacketType.HMANN) {
				p = new HirschmannPacket();
			} else {
				p = new SpamPacket();
				p.setPayload(data);
				p.setDispatchTime(System.currentTimeMillis());
				p.setSenderMeasuredPacketRate(avgPPS);
			}
			p.setMinimumSize(packetSize);
			p.setConfiguredPacketsPerSecond(senderConfiguredPacketRate);
			p.setSenderId(senderId);
			// keep sent packet count in range
			sentPacketCount = (sentPacketCount+1)%(MAX_PACKET_NO+1);
			p.setSequenceNumber(sentPacketCount);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return p;
	}
	
	
	// this is uses to analyze the queued sending intervals
	private class AnalyzeSender implements Runnable {
		
		transient private LinkedSplitQueue<Integer> data;
		transient private long counter=-1;

		public void run() {
			counter++;
			if(counter%analyzingBehaviour.getDiv() == 0) {
				// analyze if enough sending intervals are buffered
				if(sentTimes.size()>statsStepWidth) {
					data = sentTimes.split();
					// if stats are to be calced new
					int div = 0;
					double avg = 0;
					data.setIteratorStepSize(statsStepWidth);
					for(int s : data) {
						div++;
						avg += s;
					}
					avg /= div;
					double doubleMsInterval = Math.round(avg/1.0E6);
					if(doubleMsInterval > 0) {
						avg = 1.0E3 / doubleMsInterval;
						overloaded = false;
					} else {
						avg = Math.round(1.0E8/avg);
						overloaded = true;
					}
					avgPPS = Math.round(avg);
					minPPS = Math.min(minPPS, avgPPS);
					maxPPS = Math.max(maxPPS, avgPPS);
				}
			} else {
				// clear the buffer
				sentTimes.split();
			}
			fireSenderDataChangedEvent();
		}
	}
	
	// notifies all SenderDataChangeListeners about new statistical data
	private void fireSenderDataChangedEvent() {
		SenderDataChangedEvent e = new SenderDataChangedEvent(this);
		for(SenderDataChangeListener l : senderDataChangeListeners) {
			try {
				l.dataChanged(e);
			} catch(Throwable t) {
				// Ramin handling
			}
		}
	}
	
	/**
	 * @param pps to send
	 */
	public boolean setSenderConfiguredPacketRate(Object pps) {
		int pps2 = -1;
		if(pps instanceof Integer) {
			// number is given directly
			pps2 = (Integer) pps;
		} else if (pps instanceof String) {
			// try to parse string argument
			try {
				pps2 = Integer.parseInt((String) pps);
			} catch(NumberFormatException e) { // wrong argument was given
				eMan.reportErrorEvent(
					new ErrorEvent(4, "Model.Sender.PPS.Wrong.text", pps.toString())
				);
				return false;
			}
		} else {
			eMan.reportErrorEvent(
				new ErrorEvent(5, "Model.Sender.Fatal.text", "Error Code: Sender.setSenderConfiguredPPS.WrongArgument")
			);
			return false;
		}
		if(pps2 < 1) { // lower bound check
			pps2 = 1;
			eMan.reportErrorEvent(
				new ErrorEvent(3, "Model.Sender.PPS.Wrong.text", "Corrected to 1")
			);
		}
		if(pps2 > 1000) { // upper bound check
			pps2 = 1000;
			eMan.reportErrorEvent(
				new ErrorEvent(3, "Model.Sender.PPS.Wrong.text", "Corrected to 1000")
			);
		}
		this.senderConfiguredPacketRate = pps2;
		return true;
	}
	
	/**
	 * @return pps that are sent
	 */
	public int getSenderConfiguredPacketRate() {
		return senderConfiguredPacketRate;
	}

	/**
	 * @param l new Listener to subscribe SenderDataChangedEvents
	 */
	public void addSenderDataChangeListener(SenderDataChangeListener l) {
		senderDataChangeListeners.add(l);
	}
	
	/**
	 * @param l Listener to unsubscribe from SenderDataChangedEvents
	 */
	public void removeSenderDataChangeListener(SenderDataChangeListener l) {
		senderDataChangeListeners.remove(l);
	}

	/**
	 * @return the id number of this sender
	 */
	public int getSenderId() {
		return (int)senderId;
	}
	
	/**
	 * @param senderId new id number for this sender
	 */
	public void setSenderId(int senderId) {
		this.senderId = senderId; 
	}

	/**
	 * @return type of packets this sender sends
	 */
	public PacketType getpType() {
		return pType;
	}

	/**
	 * @param pType type of packets to send
	 */
	public void setpType(PacketType pType) {
		this.pType = pType;
	}

	/**
	 * @return time to live of sent packets
	 */
	public int getTtl() {
		return ttl;
	}

	/**
	 * @param ttl time to live of sent packets
	 */
	public boolean setTtl(Object ttl) {
		int ttl2 = -1;
		if(ttl instanceof Integer) {
			// number is given directly
			ttl2 = (Integer) ttl;
		} else if(ttl instanceof String) {
			// try to parse string argument
			try {
				ttl2 = Integer.parseInt((String) ttl);
			} catch (NumberFormatException e) {
				eMan.reportErrorEvent(
					new ErrorEvent(4, "Model.Sender.Ttl.Wrong.text", ttl.toString())
				);
				return false;
			}
		} else {
			// developer error
			eMan.reportErrorEvent(
				new ErrorEvent(5, "Model.Sender.Fatal.text", "Error Code: Sender.setttl.WrongArgument")
			);
			return false;
		}
		if(ttl2 < 1) { // lower bound check
			ttl2 = 1;
			eMan.reportErrorEvent(
				new ErrorEvent(3, "Model.Sender.Ttl.Wrong.text", "Corrected to 1")
			);
		}
		if(ttl2 > 255) { // upper bound check
			ttl2 = 255;
			eMan.reportErrorEvent(
				new ErrorEvent(3, "Model.Sender.Ttl.Wrong.text", "Corrected to 255")
			);
		}
		this.ttl = ttl2;
		return true;
	}

	/**
	 * @return size of sent packets in byte
	 */
	public int getPacketSize() {
		return packetSize;
	}

	/**
	 * @param packetSize size of sent packets in byte
	 */
	public boolean setPacketSize(Object packetSize) {
		int ps2 = -1;
		if(packetSize instanceof Integer) {
			// number is given directly
			ps2 = (Integer) ttl;
		} else if(packetSize instanceof String) {
			// try to parse string argument
			try {
				ps2 = Integer.parseInt((String) packetSize);
			} catch (NumberFormatException e) {
				eMan.reportErrorEvent(
					new ErrorEvent(4, "Model.Sender.PacketSize.Wrong.text", packetSize.toString())
				);
				return false;
			}
		} else {
			// developer error
			eMan.reportErrorEvent(
				new ErrorEvent(5, "Model.Sender.Fatal.text", "Error Code: Sender.setPacketSize.WrongArgument")
			);
			return false;
		}
		if(ps2 < 1) { // lower bound check
			ps2 = 1;
			eMan.reportErrorEvent(
				new ErrorEvent(3, "Model.Sender.PacketSize.Wrong.text", "Corrected to 1")
			);
		}
		if(ps2 > 9000) { // upper bound check
			ps2 = 9000;
			eMan.reportErrorEvent(
				new ErrorEvent(3, "Model.Sender.PacketSize.Wrong.text", "Corrected to 9000")
			);
		}
		this.packetSize = ps2;
		return true;
	}

	/**
	 * @return sent payload as byte array
	 */
	public byte[] getPayload() {
		return data;
	}
	
	/**
	 * @return sent payload as string or null if parsing is not possible
	 */
	public String getPayloadAsString() {
		String s = null;
		try {
			s = new String(data);
		} catch(Exception e) {}
		return s;
	}

	/**
	 * @param data sent payload from byte array
	 */
	public void setPayload(byte[] payload) {
		this.data = payload;
	}
	
	/**
	 * @param payload sent payload from string
	 */
	public void setPayloadFromString(String payload) {
		this.data = payload.getBytes();
	}

	/**
	 * @return number of overall sent packets
	 */
	public long getSentPacketCount() {
		return sentPacketCount;
	}

	/**
	 * @return average measured packets per second
	 */
	public long getAvgPPS() {
		return avgPPS;
	}

	/**
	 * @return minimum measured packets per second
	 */
	public long getMinPPS() {
		return (minPPS==Integer.MAX_VALUE) ? 0 : minPPS;
	}

	/**
	 * @return maximum measured packets per second
	 */
	public long getMaxPPS() {
		return (maxPPS==Integer.MIN_VALUE) ? 0 : maxPPS;
	}
	
	/**
	 * @return true if sender expects that it is overloaded
	 */
	public boolean isOverloaded() {
		return overloaded;
	}
	
	/**
	 * @return a map with the configuration of this sender. Is in same format as map used to
	 * create the sender with the SenderPool.
	 */
	public Map<String, String> getConfiguration() {
		HashMap<String, String> conf = new HashMap<String, String>();
		
		// get interface ip address in fitting ip mode
		Enumeration<InetAddress> addresses = this.getNetworkInterface().getInetAddresses();
		InetAddress ninf = null;
		while(addresses.hasMoreElements()) {
			ninf = addresses.nextElement();
			if(ninf.getClass().equals(ipMode)) {
				break;
			}
		}
		conf.put("ninf", ninf.getHostAddress());
		conf.put("group", this.getGroup().getHostAddress());
		conf.put("port", ""+this.getPort());
		conf.put("pps", ""+this.getSenderConfiguredPacketRate());
		conf.put("psize", ""+this.getPacketSize());
		conf.put("ttl", ""+this.getTtl());
		conf.put("payload", this.getPayloadAsString());
		conf.put("ptype", this.getpType().name());
		conf.put("abeh", this.getAnalyzingBehaviour().getIdentifier());
		
		return conf;
	}
	
	/**
	 * @return a meaningful string representation of this sender
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("--- Sender Stats for: "+senderId+" ---\n");
		sb.append("C.R.|P.Size|P.Sent|P.Type: "+getSenderConfiguredPacketRate()+"|"+getPacketSize()+"|"+getSentPacketCount()+"|"+getpType().getDisplayName()+"\n");
		sb.append("PPS (MIN|AVG|MAX): "+getMinPPS()+"|"+getAvgPPS()+"|"+getMaxPPS()+"\n");
		sb.append("----------------------------------------\n");
		return sb.toString();
	}
}
