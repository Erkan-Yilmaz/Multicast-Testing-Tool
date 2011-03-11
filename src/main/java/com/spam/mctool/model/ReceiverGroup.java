package com.spam.mctool.model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.DataFormatException;

import com.spam.mctool.model.packet.AutoPacket;
import com.spam.mctool.model.packet.Packet;

public class ReceiverGroup extends MulticastStream {
	
	ConcurrentHashMap<Long, Receiver> receivers;
	byte[] buffer;
	AnalyzeReceiverGroup analyzer;
	
	protected ReceiverGroup(ScheduledThreadPoolExecutor stpe) {
		receivers = new ConcurrentHashMap<Long, Receiver>(10);
		this.stpe = stpe;
		buffer = new byte[9000];
		analyzer = new AnalyzeReceiverGroup();
	}

	@Override
	public void activate() {
		try {
			socket = new MulticastSocket(getPort());
			socket.setNetworkInterface(getNetworkInterface());
			socket.joinGroup(group);
			state = State.ACTIVE;
			stpe.execute(this);
			stpe.scheduleAtFixedRate(analyzer, statsInterval, statsInterval, TimeUnit.MILLISECONDS);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deactivate() {
		state = State.INACTIVE;
		socket.close();
	}

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
			// create new receiver for sender id if not exists
			if(!receivers.containsKey(p.getSenderId())) {
				synchronized(receivers) {
					receivers.put(p.getSenderId(), new Receiver(p.getSenderId(), this.analyzingBehaviour));
				}
			}
			// add the packet container
			receivers.get(p.getSenderId()).addPacketContainer(con);
			// schedule the next fetch
			if(state == State.ACTIVE) {
				stpe.execute(this);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataFormatException dfe) {
			dfe.printStackTrace();
		}
	}
	
	protected static class PacketContainer {
		public long receivedTime;
		public Packet packet;
	}
	
	private class AnalyzeReceiverGroup implements Runnable {

		public void run() {
			for(Receiver r : receivers.values()) {
				System.out.println(r.getStatistics());
			}
		}
		
	}

}
