package com.spam.mctool.model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
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
			con.receivedTime = System.currentTimeMillis();
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
		Packet packet;
		int size;
	}
	
	// this is scheduled in the executor thread pool to analyze the data of the receivers
	private class AnalyzeReceiverGroup implements Runnable {
		public void run() {
			synchronized(statsLock) {
				ReceiverDataChangedEvent rdce = new ReceiverDataChangedEvent(ReceiverGroup.this);
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
		}
	}

}
