package com.spam.mctool.model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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
	
	protected Queue<Long> sentTimes;
	
	private List<SenderDataChangeListener> senderDataChangeListeners = new ArrayList<SenderDataChangeListener>();
	
	protected Sender(ScheduledThreadPoolExecutor stpe) {
		this.stpe = stpe;
		this.senderId = (long) (Long.MAX_VALUE*Math.random());
	}
	
	@Override
	public void activate() {
		try {
			this.socket = new MulticastSocket(this.getPort());
			this.socket.setTimeToLive(this.getTtl());
			this.socket.joinGroup(this.getGroup());
			long period = (long) (1E3/this.senderConfiguredPacketRate);
			this.sf = this.stpe.scheduleAtFixedRate(this, 0, period, TimeUnit.MILLISECONDS);
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
		p.setSenderMeasuredPacketRate(0);
		return p;
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
	
}
