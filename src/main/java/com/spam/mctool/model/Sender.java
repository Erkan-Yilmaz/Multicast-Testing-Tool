package com.spam.mctool.model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Multicast sender class
 * @author Jeffrey Jedele
 */
public class Sender extends MulticastStream {
	
	protected int ttl;
	
	protected int packetSize;
	
	protected Queue<Long> sentTimes;
	
	private int floatingAvgInterval = 3;
	
	protected void init() {
		sentTimes = new LinkedList<Long>();
	}

	@Override
	protected void work() {
		try {
			DatagramPacket dp = new DatagramPacket(data, data.length, group, port);
			socket.send(dp);
			sentTimes.add(System.nanoTime());
			synchronized(sentTimes) {
				sentTimes.notify();
			}
			this.sentPacketCount++;
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
		synchronized(sentTimes) {
			if(sentTimes.size() >= floatingAvgInterval*2) {
				double avg=0;
				for(int i=1; i<floatingAvgInterval; i++) {
					avg += -1*(sentTimes.poll()- sentTimes.poll());
				}
				avg /= floatingAvgInterval;

				System.out.println("Avg: "+(1.0E9/avg));
				
			}
			
			try {
				sentTimes.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String... args) {
		Sender s = new Sender();
		try {
			s.setNetworkInterface(NetworkInterface.getByName("wlan0"));
		} catch (SocketException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		s.setData(new byte[] {1,2,3,4});
		s.setSenderConfiguredPacketRate(1000);
		s.setPort(8888);
		try {
			s.setGroupByString("224.0.0.1");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		s.activate();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.deactivate();
		System.out.println(s.sentPacketCount);
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
		synchronized(sentTimes) {
			sentTimes.notifyAll();
		}
	}
	
}
