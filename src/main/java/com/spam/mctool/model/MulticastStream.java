package com.spam.mctool.model;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

/**
 * Super class of sending and receiving multicast streams
 * @author Jeffrey Jedele
 *
 */
public abstract class MulticastStream {
	
	protected InetAddress group;
	
	protected int port;
	
	protected int measuredPacketRate;
	
	protected int packetSize;
	
	protected byte[] data;
	
	protected int senderConfiguredPacketRate;
	
	protected long sentPacketCount = 0;
	
	protected int statsInterval = 10;
	protected int statsGap = 2;
	protected long statsCounter = 0;
	protected double statsDistortionLimit = 0.3;
	
	protected NetworkInterface networkInterface;
	
	protected MulticastSocket socket;
	
	protected Thread workThread;
	protected Runnable workJob = new Runnable() {
		public void run() {
			while(!jobInterrupted) {
				work();
			}
		}
	};
	
	protected Thread analyzeThread;
	protected Runnable analyzeJob = new Runnable() {
		public void run() {
			while(!jobInterrupted) {
				analyze();
			}
		}
	};
	protected boolean jobInterrupted = true;
	
	protected abstract void init();
	protected abstract void work();
	protected abstract void analyze();
	protected abstract void exit();
	
	/**
	 * activates the multicast stream
	 */
	public void activate() {
		init();
		if(group == null || port == 0 || networkInterface == null) {
			throw new IllegalStateException();
		} else {
			try {
				socket = new MulticastSocket(port);
				socket.setNetworkInterface(networkInterface);
				socket.joinGroup(group);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		jobInterrupted = false;
		workThread = new Thread(workJob);
		workThread.setName("Worker Thread");
		workThread.start();
		analyzeThread = new Thread(analyzeJob);
		analyzeThread.setName("Analyze Thread");
		analyzeThread.start();
	}
	
	/**
	 * activates the multicast stream
	 */
	public void deactivate() {
		jobInterrupted  = true;
		workThread = null;
		analyzeThread = null;
		exit();
	}

	public InetAddress getGroup() {
		return group;
	}

	public void setGroup(InetAddress group) {
		this.group = group;
	}
	
	public void setGroupByString(String group) throws UnknownHostException {
		this.group = InetAddress.getByName(group);
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getMeasuredPacketRate() {
		return measuredPacketRate;
	}

	public void setMeasuredPacketRate(int measuredPacketRate) {
		this.measuredPacketRate = measuredPacketRate;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public NetworkInterface getNetworkInterface() {
		return networkInterface;
	}

	public void setNetworkInterface(NetworkInterface networkInterface) {
		this.networkInterface = networkInterface;
	}

	public int getSenderConfiguredPacketRate() {
		return senderConfiguredPacketRate;
	}

	public long getSentPacketCount() {
		return sentPacketCount;
	}
	public int getPacketSize() {
		return packetSize;
	}
	public void setPacketSize(int packetSize) {
		this.packetSize = packetSize;
	}
        public boolean isActive() {
            return !this.jobInterrupted;
        }

}
