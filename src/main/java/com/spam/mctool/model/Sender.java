package com.spam.mctool.model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.spam.mctool.intermediates.SenderDataChangedEvent;
import com.spam.mctool.model.packet.Packet;
import com.spam.mctool.model.packet.SpamPacket;

/**
 * Multicast sender class
 * @author Jeffrey Jedele
 */
public class Sender extends MulticastStream {
	
	protected int ttl;
	
	protected long senderId;
	
	protected Queue<Long> sentTimes;
	
	protected long avgPacketRate = 0;
	protected long minPacketRate = Long.MAX_VALUE;
	protected long maxPacketRate = Long.MIN_VALUE;
	
	private List<SenderDataChangeListener> senderDataChangeListeners = new ArrayList<SenderDataChangeListener>();
	
	protected void init() {
		senderId = (long) (Long.MAX_VALUE * Math.random());
		sentTimes = new LinkedList<Long>();
	}

	@Override
	protected void work() {
		try {
			// make the packet
			Packet p = getPacket();
			p.setSequenceNumber(sentPacketCount+1);
			long dispatchTime = System.currentTimeMillis();
			p.setDispatchTime(dispatchTime);
			byte[] ba = p.toByteArray().array();
			DatagramPacket dp = new DatagramPacket(ba, ba.length, group, port);
			socket.send(dp);
			sentTimes.add(dispatchTime);
			this.sentPacketCount++;
			synchronized(sentTimes) {
				sentTimes.notify();
			}
			Thread.sleep(1000/senderConfiguredPacketRate);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void analyze() {
		if(sentTimes.size() >= statsInterval) {
			statsCounter++;
			if(statsCounter%statsGap == 0) {
				long[] times = new long[statsInterval];
				for(int i=0; i<statsInterval; i++) {
					times[i] = sentTimes.poll();
				}
				
				avgPacketRate = 0;
				for(int i=0; i<statsInterval-1; i++) {
					long packetRate = (long) (1E3 / (times[i+1]-times[i]));
					
					avgPacketRate += packetRate;
					
					if(packetRate > maxPacketRate) {
						maxPacketRate = packetRate;
					}
					
					if(packetRate < minPacketRate) {
						minPacketRate = packetRate;
					}
				}
				avgPacketRate = avgPacketRate/(statsInterval-1);
				
				fireSenderDataChangedEvent();
			} else {
				for(int i=0; i<statsInterval; i++) {
					sentTimes.poll();
				}
			}
		}
		
		synchronized(sentTimes) {			
			try {
				sentTimes.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setSenderConfiguredPacketRate(int pps) throws IllegalArgumentException {
		if(pps <= 0 || pps > 1000) {
			this.senderConfiguredPacketRate = 1;
			throw new IllegalArgumentException();
		} else {
			this.senderConfiguredPacketRate = pps;
		}
	}

	@Override
	protected void exit() {
		
	}
	
	private Packet getPacket() {
		Packet p = new SpamPacket();
		p.setConfiguredPacketsPerSecond(senderConfiguredPacketRate);
		p.setPayload(getData());
		p.setSenderId(senderId);
		p.setSenderMeasuredPacketRate(avgPacketRate);
		return p;
	}
	
	public long getAvgPacketRate() {
		return avgPacketRate;
	}

	public long getMinPacketRate() {
		return minPacketRate;
	}

	public long getMaxPacketRate() {
		return maxPacketRate;
	}

	public void addSenderDataChangeListener(SenderDataChangeListener l) {
		senderDataChangeListeners.add(l);
	}
	
	public void removeSenderDataChangeListener(SenderDataChangeListener l) {
		senderDataChangeListeners.remove(l);
	}
	
	private void fireSenderDataChangedEvent() {
		SenderDataChangedEvent e = new SenderDataChangedEvent(this);
		for(SenderDataChangeListener l : senderDataChangeListeners) {
			l.dataChanged(e);
		}
	}

	public long getSenderId() {
		return senderId;
	}
	
}
