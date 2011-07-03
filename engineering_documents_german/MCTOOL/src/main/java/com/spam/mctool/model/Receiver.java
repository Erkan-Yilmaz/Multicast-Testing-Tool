package com.spam.mctool.model;

import java.net.InetAddress;
import java.util.Date;
import com.spam.mctool.model.MulticastStream.PacketType;
import com.spam.mctool.model.ReceiverGroup.PacketContainer;
import com.spam.mctool.model.packet.HirschmannPacket;

/**
 * This class represents the datastream of single sender in a multicast
 * group on receiver side.
 * @author Jeffrey Jedele
 */
public class Receiver {
	
	// head data
	private long senderId;
	protected long lastReceivedTime = 0;
	private boolean alive;
	private boolean aliveStateChanged;
	// internals
	private LinkedSplitQueue<ReceiverGroup.PacketContainer> packetQueue;
	private MulticastStream.AnalyzingBehaviour abeh;
	private long statsCounter = 0;
	private int statsStepSize = 1;
	private long timeout = 2000;
	private long lastPacketNo = 0;
	// statistics
	private Boolean statsLock = new Boolean(false);
	private int maxDelay = Integer.MIN_VALUE;
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
	private InetAddress senderAddress;
	
	/**
	 * Default Constructor. Only called by the ReceiverGroup.
	 */
	Receiver(long senderId, MulticastStream.AnalyzingBehaviour abeh) {
		this.senderId = senderId;
		this.packetQueue = new LinkedSplitQueue<ReceiverGroup.PacketContainer>();
		this.abeh = abeh;
		this.lastPayload = new byte[1];
		this.lastPacketSize = 0;
		this.statsCounter = abeh.getDiv()-1;
	}
	
	/**
	 * Calculates new statistics for this receiver. Only called by
	 * ReceiverGroup.
	 */
	protected void calcNewStatistics() {
		// split queue
		LinkedSplitQueue<ReceiverGroup.PacketContainer> data = packetQueue.split();
		if(data.size() < 2) return;
		data.setIteratorStepSize(statsStepSize);
		int div = (int) Math.ceil(data.size()/statsStepSize)-1;
		synchronized(statsLock) {
			// initialize the average counters
			double ppsavg = 0.0;
			double travavg = 0.0;
			PacketContainer last = null;
			for (PacketContainer cur : data) {
				if (last == null) {
					last = cur;
					continue;
				}

				int delay = (int) Math
						.round((cur.receivedTime - last.receivedTime) / 1.0E6);
				if (delay > maxDelay) {
					maxDelay = delay;
				}
				ppsavg += (double) (delay) / (double) statsStepSize;
				travavg += cur.systemTime - cur.packet.getDispatchTime();
				last = cur;
			}
			ppsavg /= div;
			// translate receiving time spans in pps
			ppsavg = 1.0E3 / Math.round(ppsavg);
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
			if (((com.spam.mctool.model.packet.AutoPacket) last.packet)
					.getPacketType().equals(HirschmannPacket.class)) {
				lastPacketType = PacketType.HMANN;
			} else {
				lastPacketType = PacketType.SPAM;
			}
			senderAddress = last.address;
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
		lastReceivedTime = p.systemTime;
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
		synchronized(statsLock) {
			return senderId;
		}
	}

	/**
	 * @return if receiver has received packets in the given timeout interval
	 */
	public boolean isAlive() {
		synchronized(statsLock) {
			return alive;
		}
	}

	/**
	 * @return if alive state of this receiver has changed with this reporting cylce
	 */
	public boolean hasChangedAliveState() {
		synchronized(statsLock) {
			return aliveStateChanged;
		}
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
	 * @return packet losing rate in permille
	 */
	public double getLostPacketsPermille() {
		return (double)lostPackets/(double)receivedPackets*1000.0;
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
		synchronized (statsLock) {
			return senderConfiguredPPS;
		}
	}

	/**
	 * @return avg. pps rate measured at the sender
	 */
	public long getSenderMeasuredPPS() {
		synchronized (statsLock) {
			return senderMeasuredPPS;
		}
	}

	/**
	 * @return overall packet count sent from sender in this group
	 */
	public long getSenderSentPackets() {
		synchronized (statsLock) {
			return senderSentPackets;
		}
	}

	/**
	 * @return calculated min. packets per second
	 */
	public int getMinPPS() {
		synchronized (statsLock) {
			return minPPS == Integer.MAX_VALUE ? 0 : minPPS;
		}
	}

	/**
	 * @return calculated avg. packets per second
	 */
	public int getAvgPPS() {
		synchronized (statsLock) {
			return alive ? avgPPS : 0;
		}
	}

	/**
	 * @return calculated max. packets per second
	 */
	public int getMaxPPS() {
		synchronized (statsLock) {
			return maxPPS == Integer.MIN_VALUE ? 0 : maxPPS;
		}
	}

	/**
	 * @return max. delay between tow received packets in ms
	 */
	public int getMaxDelay() {
		synchronized (statsLock) {
			return (maxDelay>=0) ? maxDelay : 0;
		}
	}

	/**
	 * @return min. traversal time between sender and receiver in ms
	 */
	public long getMinTraversal() {
		synchronized (statsLock) {
			return minTraversal == Long.MAX_VALUE ? 0 : minTraversal;
		}
	}

	/**
	 * @return avg. traversal time between sender and receiver in ms
	 */
	public long getAvgTraversal() {
		synchronized (statsLock) {
			return avgTraversal;
		}
	}

	/**
	 * @return max traversal time between sender and receiver in ms
	 */
	public long getMaxTraversal() {
		synchronized (statsLock) {
			return maxTraversal == Long.MIN_VALUE ? 0 : maxTraversal;
		}
	}

	/**
	 * @return payload of last received packet as byte array
	 */
	public byte[] getPayload() {
		synchronized (statsLock) {
			return lastPayload;
		}
	}
	
	/**
	 * @return payload of last received packet as string, or null if parsing is not possible
	 */
	public String getPayloadAsString() {
		synchronized (statsLock) {
			String s = null;
			try {
				s = new String(lastPayload);
			} catch (Exception e) {
			}
			return s;
		}
	}

	/**
	 * @return size of the last received packet
	 */
	public long getPacketSize() {
		synchronized (statsLock) {
			return lastPacketSize;
		}
	}
	
	/**
	 * @return the types of packets received on this stream
	 */
	public PacketType getPacketType() {
		synchronized (statsLock) {
			return lastPacketType;
		}
	}
	
	/**
	 * @return address of the packet sender
	 */
	public InetAddress getSenderAddress() {
	    synchronized (statsLock) {
			return senderAddress;
		}
	}
	
	/**
	 * @param alive the alive state to set
	 */
	void setAlive(boolean alive) {
		this.alive = alive;
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
