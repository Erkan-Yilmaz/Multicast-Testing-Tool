package com.spam.mctool.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import com.spam.mctool.model.ReceiverGroup.PacketContainer;
import com.spam.mctool.model.packet.Packet;

/**
 * This class represents the datastream of single sender in a multicast
 * group on receiver side
 * @author Jeffrey Jedele
 */
public class Receiver {
	
	private long senderId;
	private long lastPacketNo = 0;
	private long lastReceivedTime = 0;
	private long lostPackets = 0;
	private long receivedPackets = 0;
	private int minPPS = Integer.MAX_VALUE;
	private int avgPPS = 0;
	private int maxPPS = Integer.MIN_VALUE;
	private int maxDelay = 0;
	private long minTraversal = Long.MAX_VALUE;
	private long avgTraversal = 0;
	private long maxTraversal = Long.MIN_VALUE;
	private LinkedSplitQueue<ReceiverGroup.PacketContainer> packetQueue;
	private MulticastStream.AnalyzingBehaviour abeh;
	private ReceiverStatistics lastStats;
	private long statsCounter = 0;
	private int statsStepSize = 1;
	private long timeout = 5000;
	
	// is automatically created by ReceiverGroup when new sender id is discovered
	Receiver(long senderId, MulticastStream.AnalyzingBehaviour abeh) {
		this.senderId = senderId;
		packetQueue = new LinkedSplitQueue<ReceiverGroup.PacketContainer>();
		this.abeh = abeh;
		this.lastStats = new ReceiverStatistics();
	}
	
	// calculated a new statistic object
	private ReceiverStatistics calcNewStatistics() {
		// split queue
		LinkedSplitQueue<ReceiverGroup.PacketContainer> data = packetQueue.split();
		data.setIteratorStepSize(statsStepSize);
		int div = data.size() / statsStepSize;
		// initialize the average counters
		double ppsavg = 0.0;
		double travavg = 0.0;
		PacketContainer last = null;
		for(PacketContainer cur : data) {
			if(last == null) {
				last = cur;
				continue;
			}
			
			int delay = (int)(cur.receivedTime-last.receivedTime);
			if(delay>maxDelay) {
				maxDelay = delay;
			}
			ppsavg += (double)(delay)/(double)statsStepSize;
			travavg += cur.receivedTime - cur.packet.getDispatchTime();
			last = cur;
		}
		ppsavg /= div;
		// translate receiving time spans in pps
		ppsavg = 1.0E3 / Math.ceil(ppsavg);
		travavg /= div;
		
		// handle pps statistics
		avgPPS = (int) Math.round(ppsavg);
		maxPPS = Math.max(maxPPS, avgPPS);
		minPPS = Math.min(minPPS, avgPPS);
		
		// handle traversal statistics
		avgTraversal = (int) Math.round(travavg);
		maxTraversal = Math.max(maxTraversal, avgTraversal);
		minTraversal = Math.min(minTraversal, avgTraversal);
		
		// compose the statistics container
		ReceiverStatistics rs = new ReceiverStatistics();
		rs.setAlive(true);
		// if receiver was not alive last time, state has changed
		if(!lastStats.isAlive()) {
			rs.setChangedAliveState(true);
		} else {
			rs.setChangedAliveState(false);
		}
		rs.setSenderId(senderId);
		rs.setSentPackets(lastPacketNo);
		rs.setReceivedPackets(receivedPackets);
		rs.setLostPackets(lostPackets);
		rs.setMaxDelay(maxDelay);
		rs.setConfiguredPPS(last.packet.getConfiguredPacketsPerSecond());
		rs.setSenderMeasuredAvgPPS(last.packet.getSenderMeasuredPacketRate());
		rs.setReceiverMeasuredMinPPS(minPPS);
		rs.setReceiverMeasuredAvgPPS(avgPPS);
		rs.setReceiverMeasuredMaxPPS(maxPPS);
		rs.setMinTraversal(minTraversal);
		rs.setAvgTraversal(avgTraversal);
		rs.setMaxTraversal(maxTraversal);
		rs.setPacketSize(last.size);
		rs.setPayload(new String(last.packet.getPayload()));
		
		return rs;
	}
	
	/**
	 * Adds a new set of Packet, receiving time and size to the queue for later statistics calculation.
	 * Automatically checks for lost packets in sender stream.
	 * @param p ReceiverGroup.PacketContainer to add.
	 */
	public void addPacketContainer(PacketContainer p) {
		packetQueue.enqueue(p);
		// if new packet is not the direct descendant of last received packet and not the first, there must be lost packets
		if((lastPacketNo+1 != p.packet.getSequenceNumber()) && (receivedPackets > 0)) {
			lostPackets += p.packet.getSequenceNumber() - lastPacketNo;
		}
		receivedPackets++;
		lastPacketNo = p.packet.getSequenceNumber();
		lastReceivedTime = p.receivedTime;
	}
	
	/**
	 * Initiates a new statistical analysis round.
	 * Dependent on the set MulticastStream.AnalyzingBehaviour this may calculate 
	 * and return new statistics or clear the cache and return the last statistics.
	 * @return the ReceiverStatistics
	 */
	public ReceiverStatistics getStatistics() {
		statsStepSize = abeh.getDynamicStatsStepWidth(avgPPS);
		if(packetQueue.size()>statsStepSize*2) {
			// do something if queue provided at least two elements for the step width
			statsCounter++;
			if(statsCounter%abeh.getDiv() == 0) {
				// calc new statistics
				return lastStats = calcNewStatistics();
			} else {
				// clear the cache
				if(lastStats.isAlive()) {
					lastStats.setChangedAliveState(false);
				} else {
					lastStats.setChangedAliveState(true);
				}
				packetQueue.split();
			}
		} else {
			// do nothing if not enough data in queue
			if(System.currentTimeMillis()-lastReceivedTime > timeout) {
				// check for timeout and alive state change
				if(lastStats.isAlive()) {
					lastStats.setChangedAliveState(true);
				} else {
					lastStats.setChangedAliveState(false);
				}
				lastStats.setAlive(false);
			}
		}
		
		return lastStats;
	}

	/**
	 * @return the set MulticastStream.AnalyzingBehaviour
	 */
	public MulticastStream.AnalyzingBehaviour getAnalyzingBehaviour() {
		return abeh;
	}

	/**
	 * Can be used to adjust the analyzing granularity of this individual sender stream
	 * @param abeh
	 */
	public void setAnalyzingBehaviour(MulticastStream.AnalyzingBehaviour abeh) {
		this.abeh = abeh;
	}

	/**
	 * @return timeout interval in ms
	 */
	public long getTimeout() {
		return timeout;
	}

	/**
	 * Can be used to set a individual timeout span. If there are no new packets from the sender
	 * in this span, it will be set to dead.
	 * @param timeout in ms
	 */
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return id of the sender this receiver analyzes
	 */
	public long getSenderId() {
		return senderId;
	}

	/**
	 * @return time when last packet was received
	 */
	public Date getLastReceivedTime() {
		return new Date(lastReceivedTime);
	}

	/**
	 * @return how much packets have been lost in the stream
	 */
	public long getLostPackets() {
		return lostPackets;
	}

	/**
	 * @return how many packets were received overall
	 */
	public long getReceivedPackets() {
		return receivedPackets;
	}

	/**
	 * @return calculated min. packets per second
	 */
	public int getMinPPS() {
		return minPPS==Integer.MAX_VALUE ? 0 : minPPS;
	}

	/**
	 * @return calculated avg. packets per second
	 */
	public int getAvgPPS() {
		return avgPPS;
	}

	/**
	 * @return calculated max. packets per second
	 */
	public int getMaxPPS() {
		return maxPPS==Integer.MIN_VALUE ? 0 : maxPPS;
	}

	/**
	 * @return max. delay between tow received packets in ms
	 */
	public int getMaxDelay() {
		return maxDelay;
	}

	/**
	 * @return min. traversal time between sender and receiver in ms
	 */
	public long getMinTraversal() {
		return minTraversal==Long.MAX_VALUE ? 0 : minTraversal;
	}

	/**
	 * @return avg. traversal time between sender and receiver in ms
	 */
	public long getAvgTraversal() {
		return avgTraversal;
	}

	/**
	 * @return max traversal time between sender and receiver in ms
	 */
	public long getMaxTraversal() {
		return maxTraversal==Long.MIN_VALUE ? 0 : maxTraversal;
	}

}
