package com.spam.mctool.model;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import com.spam.mctool.model.ReceiverGroup.PacketContainer;
import com.spam.mctool.model.packet.Packet;

public class Receiver {
	
	protected long senderId;
	protected long lastPacketNo = 0;
	protected long lostPackets = 0;
	protected long receivedPackets = 0;
	protected int minPPS = Integer.MAX_VALUE;
	protected int avgPPS = 0;
	protected int maxPPS = Integer.MIN_VALUE;
	protected int maxDelay = 0;
	protected long minTraversal = Long.MAX_VALUE;
	protected long avgTraversal = 0;
	protected long maxTraversal = Long.MIN_VALUE;
	protected LinkedSplitQueue<ReceiverGroup.PacketContainer> packetQueue;
	protected MulticastStream.AnalyzingBehaviour abeh;
	protected ReceiverStatistics lastStats;
	protected long statsCounter = 0;
	protected int statsStepSize = 1;
	
	Receiver(long senderId, MulticastStream.AnalyzingBehaviour abeh) {
		this.senderId = senderId;
		packetQueue = new LinkedSplitQueue<ReceiverGroup.PacketContainer>();
		this.abeh = abeh;
		this.lastStats = new ReceiverStatistics();
	}
	
	public void addPacketContainer(PacketContainer p) {
		packetQueue.enqueue(p);
		if((lastPacketNo+1 != p.packet.getSequenceNumber()) && (receivedPackets > 0)) {
			lostPackets++;
		}
		receivedPackets++;
		lastPacketNo = p.packet.getSequenceNumber();
	}
	
	public ReceiverStatistics getStatistics() {
		statsStepSize = abeh.getDynamicStatsStepWidth(avgPPS);
		if(packetQueue.size()>statsStepSize*2) {
			statsCounter++;
			if(statsCounter%abeh.getDiv() == 0) {
				return lastStats = calcNewStatistics();
			} else {
				packetQueue.split();
			}
		}
		
		return lastStats;
	}
	
	protected ReceiverStatistics calcNewStatistics() {
		LinkedSplitQueue<ReceiverGroup.PacketContainer> data = packetQueue.split();
		int step = statsStepSize;
		data.setIteratorStepSize(step);
		int div = data.size() / step;
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
			ppsavg += (double)(delay)/(double)step;
			travavg += cur.receivedTime - cur.packet.getDispatchTime();
			last = cur;
		}
		ppsavg /= div;
		ppsavg = 1.0E3 / Math.ceil(ppsavg);
		travavg /= div;
		
		avgPPS = (int) Math.round(ppsavg);
		if(avgPPS > maxPPS) {
			maxPPS = avgPPS;
		}
		if(avgPPS < minPPS) {
			minPPS = avgPPS;
		}
		avgTraversal = (int) Math.round(travavg);
		if(avgTraversal > maxTraversal) {
			maxTraversal = avgTraversal;
		}
		if(avgTraversal < minTraversal) {
			minTraversal = avgTraversal;
		}
		
		ReceiverStatistics rs = new ReceiverStatistics();
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
		//TODO Implement Packet Size
		rs.setPacketSize(0);
		rs.setPayload(new String(last.packet.getPayload()));
		
		return rs;
	}

}
