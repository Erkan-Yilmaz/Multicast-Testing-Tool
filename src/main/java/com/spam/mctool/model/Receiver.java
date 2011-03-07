package com.spam.mctool.model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.zip.DataFormatException;

import com.spam.mctool.intermediates.ReceiverDataChangedEvent;
import com.spam.mctool.model.packet.Packet;
import com.spam.mctool.model.packet.SpamPacket;

public class Receiver extends MulticastStream {
	
	private class PacketContainer {
		public Packet packet;
		public long received;
	}
	
	private long lostPackets = 0;
	private int faultyPackets = 0;
	
	private long avgTraversal = 0;
	private long minTraversal = Long.MAX_VALUE;
	private long maxTraversal = Long.MIN_VALUE;
	
	private long avgPacketRate = 0;
	private long minPacketRate = Long.MAX_VALUE;
	private long maxPacketRate = Long.MIN_VALUE;
	
	private long senderMeasuredRate = 0;
	
	private long senderConfiguredRate = 0;
	private long senderId;
	private String senderPayload;
	
	private long receivedCounter = 0;
	private long lastPacket = -1;
	
	private Queue<PacketContainer> receivedPackets;
	
	private List<ReceiverDataChangeListener> receiverDataChangeListeners = new ArrayList<ReceiverDataChangeListener>();

	@Override
	protected void init() {
		receivedPackets = new LinkedList<PacketContainer>();
	}
	
	@Override
	protected void analyze() {
		if((receivedPackets.size() >= statsInterval)) {
			statsCounter++;
			if(statsCounter%statsGap==0) {
				PacketContainer[] packets = new PacketContainer[statsInterval];
				// fetch packets
				for(int i=0; i<statsInterval; i++) {
					packets[i] = receivedPackets.poll();
				}
				
				// traversal
				avgTraversal = 0;
				for(int i=0; i<statsInterval; i++) {
					long trav = packets[i].received-packets[i].packet.getDispatchTime();
					avgTraversal += trav;
					if(trav > maxTraversal) {
						maxTraversal = trav;
					}
					if(trav < minTraversal) {
						minTraversal = trav;
					}
				}
				avgTraversal = avgTraversal/statsInterval;
				
				// receiving intervals
				avgPacketRate = 0;
				for(int i=0; i<statsInterval-1; i++) {
					long packetRate = (long) (1E3/(packets[i+1].received - packets[i].received));
					avgPacketRate += packetRate;
					if(packetRate > maxPacketRate) {
						maxPacketRate = packetRate;
					}
					if(packetRate < minPacketRate) {
						minPacketRate = packetRate;
					}
				}
				avgPacketRate = avgPacketRate/(statsInterval-1);
				
				// sender measured rate
				senderMeasuredRate = 0;
				for(int i=0; i<statsInterval; i++) {
					senderMeasuredRate += packets[i].packet.getSenderMeasuredPacketRate();
				}
				senderMeasuredRate /= statsInterval;
				
				// one time data updates
				senderConfiguredRate = packets[statsInterval-1].packet.getConfiguredPacketsPerSecond();
				senderPayload = new String(packets[statsInterval-1].packet.getPayload());
				senderId = packets[statsInterval-1].packet.getSenderId();
				
				fireReceiverDataChangedEvent();
			} else {
				for(int i=0; i<statsInterval; i++) {
					receivedPackets.poll();
				};
			}
		}
		if(!jobInterrupted) {
			synchronized(receivedPackets) {
				try {
					receivedPackets.wait();
				} catch (InterruptedException e) {}
			}
		}
	}

	@Override
	protected void work() {
		byte[] buffer = new byte[packetSize];
		DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
		PacketContainer c = new PacketContainer();
		try {
			socket.receive(dp);
			c.received = System.currentTimeMillis();
			c.packet = getPacket(buffer);
			// look for lost packets
			if(!(c.packet.getSequenceNumber() == lastPacket+1)) {
				if(!(lastPacket==-1)) {
					lostPackets++;
				}
			}
			lastPacket = c.packet.getSequenceNumber();
			receivedPackets.add(c);
			receivedCounter++;
			synchronized(receivedPackets) {
				receivedPackets.notify();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void exit() {
		synchronized(receivedPackets) {
			receivedPackets.notifyAll();
		}
		while(!receivedPackets.isEmpty()) {
			if(receivedPackets.size()>=statsInterval) {
				analyze();
			} else {
				receivedPackets.clear();
			}
		}
	}
	
	private Packet getPacket(byte[] buffer) {
		Packet p = new SpamPacket();
		try {
			p.fromByteArray(ByteBuffer.wrap(buffer));
		} catch (DataFormatException e) {
			faultyPackets++;
		}
		return p;
	}
	

	public long getAvgTraversal() {
		return avgTraversal;
	}

	public long getMinTraversal() {
		return minTraversal;
	}

	public long getMaxTraversal() {
		return maxTraversal;
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

	public long getLostPackets() {
		return lostPackets;
	}

	public int getFaultyPackets() {
		return faultyPackets;
	}

	public long getSenderMeasuredRate() {
		return senderMeasuredRate;
	}

	public long getSenderConfiguredRate() {
		return senderConfiguredRate;
	}

	public long getSenderId() {
		return senderId;
	}

	public String getSenderPayload() {
		return senderPayload;
	}

	public long getSenderPacketCount() {
		return lastPacket;
	}
	
	public long getReceivedPacketCount() {
		return receivedCounter;
	}

	public void addReceiverDataChangeListener(ReceiverDataChangeListener l) {
		receiverDataChangeListeners.add(l);
	}
	
	public void removeReceiverDataChangeListener(ReceiverDataChangeListener l) {
		receiverDataChangeListeners.remove(l);
	}
	
	private void fireReceiverDataChangedEvent() {
		ReceiverDataChangedEvent e = new ReceiverDataChangedEvent(this);
		for(ReceiverDataChangeListener l : receiverDataChangeListeners) {
			l.dataChanged(e);
		}
	}

}
