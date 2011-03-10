package com.spam.mctool.model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.spam.mctool.intermediates.SenderDataChangedEvent;
import com.spam.mctool.model.packet.HirschmannPacket;
import com.spam.mctool.model.packet.Packet;
import com.spam.mctool.model.packet.SpamPacket;

/**
 * Multicast sender class
 * @author Jeffrey Jedele
 */
public class Sender extends MulticastStream {
	
	public static enum PacketType {
		SPAM("Spam Packet Format"),
		HMANN("Hirschmann Packet Format");
		
		private String dispName;
		
		PacketType(String dispName) {
			this.dispName = dispName;
		}
		
		public String getDisplayName() {
			return this.dispName;
		}
		
		public static PacketType getByIdentifier(String ident) {
			if(ident.equals("hmann")) {
				return PacketType.HMANN;
			} else {
				return PacketType.SPAM;
			}
		}
	}
	
	protected PacketType pType;
	
	protected byte ttl;
	
	protected long senderId;
	
	private long lastSent = 0;
	private long nowSent = 0;
	private BlockingQueue<Short> sentTimes;

	private List<SenderDataChangeListener> senderDataChangeListeners = new ArrayList<SenderDataChangeListener>();
	
	private AnalyzeSender analyzer;
	
	private long statsInterval;
	private double statsTestamount;
	
	private long avgPPS;
	private long minPPS = Integer.MAX_VALUE;
	private long maxPPS = Integer.MIN_VALUE;
	
	protected Sender(ScheduledThreadPoolExecutor stpe, long statsInterval, double statsTestamount) {
		this.stpe = stpe;
		this.sentTimes = new LinkedBlockingQueue<Short>();
		this.senderId = (long) (Long.MAX_VALUE*Math.random());
		this.analyzer = new AnalyzeSender();
		this.statsInterval = statsInterval;
		this.statsTestamount = statsTestamount;
	}
	
	@Override
	public void activate() {
		try {
			this.socket = new MulticastSocket(this.getPort());
			this.socket.setNetworkInterface(this.getNetworkInterface());
			this.socket.setTimeToLive(this.getTtl());
			this.socket.joinGroup(this.getGroup());
			long period = (long) (1E3/this.senderConfiguredPacketRate);
			this.sf = this.stpe.scheduleAtFixedRate(this, 0, period, TimeUnit.MILLISECONDS);
			this.asf = this.stpe.scheduleWithFixedDelay(analyzer, statsInterval, statsInterval, TimeUnit.MILLISECONDS);
			this.state = State.ACTIVE;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void deactivate() {
		sf.cancel(true);
		this.socket.close();
		this.state = State.INACTIVE;
	}
	
	public void run() {
		try {
			byte[] p = this.getPacket().toByteArray().array();
			DatagramPacket dp = new DatagramPacket(p, p.length, this.getGroup(), this.getPort());
			this.socket.send(dp);
			// no synchronization needed because usage of java concurrency api
			nowSent = System.currentTimeMillis();
			this.sentTimes.add((short)(nowSent-lastSent));
			lastSent = nowSent;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setSenderConfiguredPacketRate(int pps) throws IllegalArgumentException {
		this.senderConfiguredPacketRate = pps;
	}
	
	private Packet getPacket() {
		Packet p;
		switch(this.pType) {
		case HMANN:
			p = new HirschmannPacket();
			break;
		default:
			p = new SpamPacket();
		}
		p.setConfiguredPacketsPerSecond(senderConfiguredPacketRate);
		p.setPayload(getData());
		p.setSenderId(senderId);
		p.setDispatchTime(System.currentTimeMillis());
		p.setSequenceNumber(++sentPacketCount);
		p.setSenderMeasuredPacketRate(avgPPS);
		return p;
	}
	
	private class AnalyzeSender implements Runnable {
		
		private List<Short> data = new LinkedList<Short>();

		public void run() {
			if(sentTimes.size()*statsTestamount>2) { 
				data.clear();
				sentTimes.drainTo(data);
				data.remove(0);
				int amount = (int) (data.size()*statsTestamount);
				int step = data.size()/amount;
				int pos = 0;
				int valcnt = 0;
				double avg = 0;
				while(pos+step < data.size()) {
					avg += data.get(pos);
					valcnt++;
					pos += step;
				}
				avg /= valcnt; 
				avg = 1.0E3 / avg;
				avgPPS = Math.round(avg);
				if(avgPPS < minPPS) {
					minPPS = avgPPS;
				}
				if(avgPPS > maxPPS) {
					maxPPS = avgPPS;
				}
				fireSenderDataChangedEvent();
			}
		}
		
	}

	public void addSenderDataChangeListener(SenderDataChangeListener l) {
		senderDataChangeListeners.add(l);
	}
	
	public void removeSenderDataChangeListener(SenderDataChangeListener l) {
		senderDataChangeListeners.remove(l);
	}
	
	private void fireSenderDataChangedEvent() {
		SenderStatistics stats = new SenderStatistics();
		stats.setMinPPS(minPPS);
		stats.setAvgPPS(avgPPS);
		stats.setMaxPPS(maxPPS);
		stats.setSentPackets(sentPacketCount);
		SenderDataChangedEvent e = new SenderDataChangedEvent(this, stats);
		for(SenderDataChangeListener l : senderDataChangeListeners) {
			l.dataChanged(e);
		}
	}

	public long getSenderId() {
		return senderId;
	}

	public PacketType getpType() {
		return pType;
	}

	public void setpType(PacketType pType) {
		this.pType = pType;
	}

	public byte getTtl() {
		return ttl;
	}

	public void setTtl(byte ttl) {
		this.ttl = ttl;
	}

	protected BlockingQueue<Short> getSentTimes() {
		return sentTimes;
	}
	
}
