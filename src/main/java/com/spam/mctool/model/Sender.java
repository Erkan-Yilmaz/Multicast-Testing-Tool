package com.spam.mctool.model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.spam.mctool.intermediates.SenderDataChangedEvent;
import com.spam.mctool.model.packet.HirschmannPacket;
import com.spam.mctool.model.packet.Packet;
import com.spam.mctool.model.packet.SpamPacket;

/**
 * This class represents a sender which sends in a multicast group.
 * @author Jeffrey Jedele
 */
public class Sender extends MulticastStream {
	
	// internals
	private AnalyzeSender analyzer;
	private LinkedSplitQueue<Integer> sentTimes;
	private List<SenderDataChangeListener> senderDataChangeListeners = new ArrayList<SenderDataChangeListener>();
	private long lastSent = 0;
	private long nowSent = 0;
	private long lastSentPacketNo = 0;
	private Map<Long, Exception> exceptions;
	// sender specific
	private long senderId;
	private byte ttl;
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
		this.stpe = stpe;
		this.sentTimes = new LinkedSplitQueue<Integer>();
		this.senderId = (long) (Integer.MAX_VALUE*Math.random());
		this.analyzer = new AnalyzeSender();
		this.exceptions = new LinkedHashMap<Long, Exception>();
	}
	
	/**
	 * Activates the stream and analyzing behaviour.
	 */
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
			exceptions.put(System.currentTimeMillis(), e);
		}
	}
	
	/**
	 * Deactivates the stream and analyzing behaviour.
	 */
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
			DatagramPacket dp = new DatagramPacket(p, packetSize, this.getGroup(), this.getPort());
			this.socket.send(dp);
			// no synchronization needed because usage of java concurrency api
			nowSent = System.nanoTime();
			if(sentPacketCount == lastSentPacketNo+1) {
				this.sentTimes.enqueue((int)(nowSent-lastSent));
			}
			lastSent = nowSent;
			lastSentPacketNo = sentPacketCount;
		} catch (Exception e) {
			e.printStackTrace();
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
			}
			p.setMinimumSize(packetSize);
			p.setConfiguredPacketsPerSecond(senderConfiguredPacketRate);
			p.setSenderId(senderId);
			p.setDispatchTime(System.currentTimeMillis());
			p.setSequenceNumber(++sentPacketCount);
			p.setSenderMeasuredPacketRate(avgPPS);
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
			l.dataChanged(e);
		}
	}
	
	/**
	 * @param pps to send
	 */
	public void setSenderConfiguredPacketRate(int pps) {
		this.senderConfiguredPacketRate = pps;
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
	public byte getTtl() {
		return ttl;
	}

	/**
	 * @param ttl time to live of sent packets
	 */
	public void setTtl(byte ttl) {
		this.ttl = ttl;
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
	public void setPacketSize(int packetSize) {
		this.packetSize = packetSize;
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
		return (minPPS==Long.MAX_VALUE) ? 0 : minPPS;
	}

	/**
	 * @return maximum measured packets per second
	 */
	public long getMaxPPS() {
		return (maxPPS==Long.MIN_VALUE) ? 0 : maxPPS;
	}
	
	/**
	 * @return true if sender expects that it is overloaded
	 */
	public boolean isOverloaded() {
		return overloaded;
	}

	/**
	 * @return caught exceptions
	 */
	public Map<Long, Exception> getExceptions() {
		return Collections.unmodifiableMap(exceptions);
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
