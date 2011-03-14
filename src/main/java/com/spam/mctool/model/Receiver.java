package com.spam.mctool.model;

import java.util.Date;

import com.spam.mctool.model.MulticastStream.PacketType;
import com.spam.mctool.model.ReceiverGroup.PacketContainer;
import com.spam.mctool.model.packet.HirschmannPacket;

/**
 * This class represents the datastream of single sender in a multicast
 * group on receiver side
 * @author Jeffrey Jedele
 */
public class Receiver {
	
	// head data
	private long senderId;
	private long lastReceivedTime = 0;
	private boolean alive;
	private boolean aliveStateChanged;
	// internals
	private LinkedSplitQueue<ReceiverGroup.PacketContainer> packetQueue;
	private MulticastStream.AnalyzingBehaviour abeh;
	private long statsCounter = -1;
	private int statsStepSize = 1;
	private long timeout = 2000;
	private long lastPacketNo = 0;
	// statistics
	private int maxDelay = 0;
	private long receivedPackets = 0;
	private long lostPackets = 0;
	private long senderConfiguredPPS = 0;
	private long senderMeasuredPPS = 0;
	private long senderSentPackets = 0;
	private int minPPS = Integer.MAX_VALUE;
	private int avgPPS = 0;
	private int maxPPS = Integer.MIN_VALUE;
	private long minTraversal = Long.MAX_VALUE;
	private long avgTraversal = 0;
	private long maxTraversal = Long.MIN_VALUE;
	private byte[] lastPayload;
	private long lastPacketSize;
	private PacketType lastPacketType;
	
	// is automatically created by ReceiverGroup when new sender id is discovered
	Receiver(long senderId, MulticastStream.AnalyzingBehaviour abeh) {
		this.senderId = senderId;
		this.packetQueue = new LinkedSplitQueue<ReceiverGroup.PacketContainer>();
		this.abeh = abeh;
		this.lastPayload = new byte[1];
		this.lastPacketSize = 0;
	}
	
	// calculated a new statistic object
	private void calcNewStatistics() {
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
		
		// handle other statistics and data
		senderConfiguredPPS = last.packet.getConfiguredPacketsPerSecond();
		senderMeasuredPPS = last.packet.getSenderMeasuredPacketRate();
		senderSentPackets = last.packet.getSequenceNumber();
		lastPayload = last.packet.getPayload();
		lastPacketSize = last.packet.getSize();
		if(last.packet instanceof HirschmannPacket) {
			lastPacketType = PacketType.HMANN;
		} else {
			lastPacketType = PacketType.SPAM;
		}
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
	 * new statistics or only clear the cache.
	 */
	public void proposeStatisticsRenewal() {
		statsStepSize = abeh.getDynamicStatsStepWidth(avgPPS);
		if(packetQueue.size()>statsStepSize*2) {
			// do something if queue provided at least two elements for the step width
			statsCounter++;
			if(statsCounter%abeh.getDiv() == 0) {
				// calc new statistics
				calcNewStatistics();
			} else {
				// clear the cache
				packetQueue.split();
			}
		}
		
		if( System.currentTimeMillis()-lastReceivedTime <= timeout ) {
			aliveStateChanged = !alive;
			alive = true;
		} else {
			aliveStateChanged = alive;
			alive = false;
		}
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
	 * @return if receiver has received packets in the given timeout interval
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * @return if alive state of this receiver has changed with this reporting cylce
	 */
	public boolean hasChangedAliveState() {
		return aliveStateChanged;
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
	 * @return pps rate that was configured at the sender
	 */
	public long getSenderConfiguredPPS() {
		return senderConfiguredPPS;
	}

	/**
	 * @return avg. pps rate measured at the sender
	 */
	public long getSenderMeasuredPPS() {
		return senderMeasuredPPS;
	}

	/**
	 * @return overall packet count sent from sender in this group
	 */
	public long getSenderSentPackets() {
		return senderSentPackets;
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

	/**
	 * @return payload of last received packet as byte array
	 */
	public byte[] getPayload() {
		return lastPayload;
	}
	
	/**
	 * @return payload of last received packet as string, or null if parsing is not possible
	 */
	public String getPayloadAsString() {
		String s = null;
		try {
			s = new String(lastPayload);
		} catch(Exception e) {}
		return s;
	}

	/**
	 * @return size of the last received packet
	 */
	public long getPacketSize() {
		return lastPacketSize;
	}
	
	/**
	 * @return the types of packets received on this stream
	 */
	public PacketType getPacketType() {
		return lastPacketType;
	}
	
	/**
	 * @return a meaningful string representation of the state of this receiver
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("--- Receiver Stats for: "+getSenderId()+"---\n");
		sb.append("Div (R.P.|L.P.|M.Del|P.Type): "+getReceivedPackets()+"|"+getLostPackets()+"|"+getMaxDelay()+"|"+getPacketType().getDisplayName()+"\n");
		sb.append("Sender (C.PPS|M.PPS|S.P|P.S): "+getSenderConfiguredPPS()+"|"+getSenderMeasuredPPS()+"|"+getSenderSentPackets()+"|"+getPacketSize()+"\n");
		sb.append("R.PPS (MIN|AVG|MAX): "+getMinPPS()+"|"+getAvgPPS()+"|"+getMaxPPS()+"\n");
		sb.append("R.Trav (MIN|AVG|MAX): "+getMinTraversal()+"|"+getAvgTraversal()+"|"+getMaxTraversal()+"\n");
		sb.append("Payload: "+getPayloadAsString()+"\n");
		sb.append("Alive (Changed): "+isAlive()+"|"+hasChangedAliveState()+"\n");
		sb.append("-----------------------------------------------\n");
		return sb.toString();
	}

}
