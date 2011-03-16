package com.spam.mctool.model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.DataFormatException;
import com.spam.mctool.intermediates.ReceiverDataChangedEvent;
import com.spam.mctool.model.packet.AutoPacket;
import com.spam.mctool.model.packet.Packet;

/**
 * ReceiverGroup represents a subscriped multicast group on this machine.
 * It maintains several Receivers for every sending data stream discovered
 * in this group.
 * @author Jeffrey Jedele
 */
public class ReceiverGroup extends MulticastStream {
	
	// internals
	private Map<Long, Exception> exceptions;
	private Map<Long, Receiver> receivers;
	private byte[] buffer;
	private AnalyzeReceiverGroup analyzer;
	private long faultyPackets;
	private List<ReceiverDataChangeListener> rdclListeners;
	// overall statistics
	private Object statsLock = new Object();
	private long maxDelay = 0;
	private long receivedPackets = 0;
	private long lostPackets = 0;
	private long senderConfiguredPPS = 0;
	private long senderMeasuredPPS = 0;
	private long senderSentPackets = 0;
	private long minPPS = 0;
	private long avgPPS = 0;
	private long maxPPS = 0;
	private long minTraversal = 0;
	private long avgTraversal = 0;
	private long maxTraversal = 0;
	
	/**
	 * Used from the SenderManager to create a new ReceiverGroup
	 * @param stpe executing thread pool
	 */
	protected ReceiverGroup(ScheduledThreadPoolExecutor stpe) {
		this.exceptions = new LinkedHashMap<Long, Exception>();
		this.receivers = new ConcurrentHashMap<Long, Receiver>(10);
		this.stpe = stpe;
		this.buffer = new byte[10000];
		this.analyzer = new AnalyzeReceiverGroup();
		this.faultyPackets = 0;
		this.rdclListeners = new LinkedList<ReceiverDataChangeListener>();
	}

	@Override
	public void activate() {
		try {
			// open socket and join group
			socket = new MulticastSocket(getPort());
			socket.setNetworkInterface(getNetworkInterface());
			socket.joinGroup(group);
			// start rolling
			state = State.ACTIVE;
			stpe.execute(this);
			asf = stpe.scheduleAtFixedRate(analyzer, statsInterval, statsInterval, TimeUnit.MILLISECONDS);
		} catch(Exception e) {
			this.exceptions.put(System.currentTimeMillis(), e);
		}
	}

	/**
	 * Deactivate this ReceiverGroup
	 */
	@Override
	public void deactivate() {
		asf.cancel(false);
		state = State.INACTIVE;
		socket.close();
	}

	/**
	 * This is to be executed by the concurrency executor framework.
	 */
	// fetches exactly one packet and schedules the job again
	public void run() {
		DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
		try {
			// receive and parse the packet
			PacketContainer con = new PacketContainer();
			socket.receive(dp);
			con.receivedTime = System.nanoTime();
			con.systemTime = System.currentTimeMillis();
			ByteBuffer buf = ByteBuffer.wrap(buffer, 0, dp.getLength());
			Packet p = new AutoPacket();
			p.fromByteArray(buf);
			con.packet = p;
			con.size = dp.getLength();
			// create new receiver for sender id if not exists
			if(!receivers.containsKey(p.getSenderId())) {
				receivers.put(p.getSenderId(), new Receiver(p.getSenderId(), this.analyzingBehaviour));
			}
			// add the packet container
			receivers.get(p.getSenderId()).addPacketContainer(con);
			// schedule the next fetch
			if(state == State.ACTIVE) {
				stpe.execute(this);
			}
		} catch (IOException e) {
			exceptions.put(System.currentTimeMillis(), e);
		} catch (DataFormatException dfe) {
			faultyPackets++;
		}
	}
	
	/**
	 * Simple datastructure to store packets, receiving time and packet size
	 */
	protected static class PacketContainer {
		long receivedTime;
		long systemTime;
		Packet packet;
		int size;
	}
	
	// this is scheduled in the executor thread pool to analyze the data of the receivers
	private class AnalyzeReceiverGroup implements Runnable {
		public void run() {
			ReceiverDataChangedEvent rdce = new ReceiverDataChangedEvent(ReceiverGroup.this);
			
			synchronized(statsLock) {
				receivedPackets = lostPackets = senderConfiguredPPS = senderMeasuredPPS = senderSentPackets = avgPPS = avgTraversal = 0;
				minPPS = minTraversal = Long.MAX_VALUE;
				maxDelay = maxPPS = maxTraversal = Long.MIN_VALUE;
				int valcnt = 0;
				for(Receiver r : receivers.values()) {
					if(r != null) {
						valcnt++;
						r.proposeStatisticsRenewal();
						maxDelay = Math.max(maxDelay, r.getMaxDelay());
						receivedPackets += r.getReceivedPackets();
						lostPackets += r.getLostPackets();
						senderConfiguredPPS += r.getSenderConfiguredPPS();
						senderMeasuredPPS += r.getSenderMeasuredPPS();
						senderSentPackets += r.getSenderSentPackets();
						minPPS = Math.min(minPPS, r.getMinPPS());
						avgPPS += r.getAvgPPS();
						maxPPS = Math.max(maxPPS, r.getMaxPPS());
						minTraversal = Math.min(minTraversal, r.getMinTraversal());
						avgTraversal += r.getAvgTraversal();
						maxTraversal = Math.max(maxTraversal, r.getMaxTraversal());
						rdce.getReceiverList().add(r);
					}
				}
				avgPPS /= valcnt;
				avgTraversal /= valcnt;
			}
			
			fireReceiverDataChangedEvent(rdce);
		}
	}
	
	/**
	 * Add a new ReceiverDataChangeListener to the ReceiverGroup
	 * @param l
	 */
	public void addReceiverDataChangeListener(ReceiverDataChangeListener l) {
		this.rdclListeners.add(l);
	}
	
	/**
	 * Remove a ReceiverDataChangeListener from the ReceiverGroup
	 * @param l
	 */
	public void removeReceiverDataChangeListener(ReceiverDataChangeListener l) {
		this.rdclListeners.remove(l);
	}
	
	// used to fire an event to all listeners
	private void fireReceiverDataChangedEvent(ReceiverDataChangedEvent e) {
		for(ReceiverDataChangeListener l : rdclListeners) {
			l.dataChanged(e);
		}
	}
	
	/**
	 * Removes a receiver from the list, no additional statistics will be send till new packets arrive
	 * @param r a Receiver or a String with the sender id the Receiver listens to
	 */
	public void removeReceiver(Object r) {
		if(r instanceof String) {
			receivers.remove(r);
		}
		if(r instanceof Receiver) {
			receivers.remove(((Receiver)r).getSenderId());
		}
	}

	/**
	 * @return exceptions that were caught
	 */
	public Map<Long, Exception> getExceptions() {
		return exceptions;
	}

	/**
	 * @return number of packets that could not be parsed
	 */
	public long getFaultyPackets() {
		return faultyPackets;
	}

	/**
	 * @return overall maximal delay for all receivers
	 */
	public long getMaxDelay() {
		return maxDelay;
	}

	/**
	 * @return overall received packets
	 */
	public long getReceivedPackets() {
		return receivedPackets;
	}

	/**
	 * @return overall lost packets
	 */
	public long getLostPackets() {
		return lostPackets;
	}

	/**
	 * @return sum of configured packet rates of all senders in this group
	 */
	public long getSenderConfiguredPPS() {
		return senderConfiguredPPS;
	}

	/**
	 * @return sum of measured packet rates from all senders in this group
	 */
	public long getSenderMeasuredPPS() {
		return senderMeasuredPPS;
	}

	/**
	 * @return sum of sent packets for all senders on this group
	 */
	public long getSenderSentPackets() {
		return senderSentPackets;
	}

	/**
	 * @return overall minimum measured pps
	 */
	public long getMinPPS() {
		return minPPS;
	}

	/**
	 * @return overall maximum measured pps
	 */
	public long getMaxPPS() {
		return maxPPS;
	}

	/**
	 * @return overall minimum traversal time in ms
	 */
	public long getMinTraversal() {
		return minTraversal;
	}

	/**
	 * @return overall maximum traversal time in ms
	 */
	public long getMaxTraversal() {
		return maxTraversal;
	}
	
	/**
	 * @return overall measured pps
	 */
	public long getAvgPPS() {
		return avgPPS;
	}

	/**
	 * @return overall avg traversal time in ms
	 */
	public long getAvgTraversal() {
		return avgTraversal;
	}

	/**
	 * @return a meaningful string representation of the state of this receiver
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("--- ReceiverGroup Stats for: "+group+"---\n");
		sb.append("Div (R.P.|L.P.|M.Del): "+getReceivedPackets()+"|"+getLostPackets()+"|"+getMaxDelay()+"\n");
		sb.append("Sender (C.PPS|M.PPS|S.P): "+getSenderConfiguredPPS()+"|"+getSenderMeasuredPPS()+"|"+getSenderSentPackets()+"\n");
		sb.append("R.PPS (MIN|AVG|MAX): "+getMinPPS()+"|"+getAvgPPS()+"|"+getMaxPPS()+"\n");
		sb.append("R.Trav (MIN|AVG|MAX): "+getMinTraversal()+"|"+getAvgTraversal()+"|"+getMaxTraversal()+"\n");
		sb.append("-----------------------------------------------\n");
		return sb.toString();
	}

}
