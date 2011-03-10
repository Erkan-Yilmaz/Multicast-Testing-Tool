package com.spam.mctool.model;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.spam.mctool.intermediates.SenderAddedOrRemovedEvent;

public class SenderPool implements SenderManager {
	private ScheduledThreadPoolExecutor stfe;
	private Set<Sender> senders = new HashSet<Sender>();
	private List<SenderAddedOrRemovedListener> saorl;
	private int statsInterval;
	private double statsTestamount;
	
	public SenderPool() {
		this.saorl = new LinkedList<SenderAddedOrRemovedListener>();
		this.stfe = new ScheduledThreadPoolExecutor(30);
		this.statsInterval = 500;
		this.statsTestamount = 0.3;
	}

	public Sender create(Map<String, String> params) throws IllegalArgumentException {
		Sender sender;
		InetAddress group;
		int port;
		byte ttl;
		int pps;
		int psize;
		byte[] payload;
		Sender.PacketType ptype;
		NetworkInterface ninf;
		
		try {
			group = InetAddress.getByName(params.get("group"));
			ninf = NetworkInterface.getByInetAddress(InetAddress.getByName(params.get("ninf")));
			port = new Integer(params.get("port"));
			ttl = new Byte(params.get("ttl"));
			if(ttl<=0 || ttl>255) throw new Exception();
			pps = new Integer(params.get("pps"));
			if(pps<=0 || pps>1000) throw new Exception();
			psize = new Integer(params.get("psize"));
			if(psize<150 || psize>9000) throw new Exception();
			String pstring = params.get("payload");
			if(pstring == null) {
				pstring = "";
			}
			payload = pstring.getBytes();
			ptype = Sender.PacketType.getByIdentifier(params.get("ptype"));
		} catch(Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		}
		
		sender = new Sender(this.stfe, this.statsInterval, this.statsTestamount);
		sender.setGroup(group);
		sender.setPort(port);
		sender.setTtl(ttl);
		sender.setSenderConfiguredPacketRate(pps);
		sender.setPacketSize(psize);
		sender.setData(payload);
		sender.setpType(ptype);
		sender.setNetworkInterface(ninf);
		
		this.senders.add(sender);
		this.fireSenderAddedEvent(sender);
		return sender;
	}

	public void remove(Sender sender) {
		sender.deactivate();
		this.fireSenderRemovedEvent(sender);
	}

	public Collection<Sender> getSenders() {
		return senders;
	}

	public void killAll() {
		for(Sender s : senders) {
			if(s.getState() == MulticastStream.State.ACTIVE) {
				s.deactivate();
			}
		}
		this.stfe.shutdown();
	}

	public void addSenderAddedOrRemovedListener(SenderAddedOrRemovedListener l) {
		synchronized(this.saorl) {
			this.saorl.add(l);
		}
	}

	public void removeSenderAddedOrRemovedListener(SenderAddedOrRemovedListener l) {
		synchronized(this.saorl) {
			this.saorl.remove(l);
		}
	}
	
	private void fireSenderAddedEvent(Sender s) {
		synchronized(this.saorl) {
			for(SenderAddedOrRemovedListener l : this.saorl) {
				l.senderAdded(new SenderAddedOrRemovedEvent(s));
			}
		}
	}
	
	private void fireSenderRemovedEvent(Sender s) {
		synchronized(this.saorl) {
			for(SenderAddedOrRemovedListener l : this.saorl) {
				l.senderRemoved(new SenderAddedOrRemovedEvent(s));
			}
		}
	}
	
	public int getStatsInterval() {
		return statsInterval;
	}

	public void setStatsInterval(int statsInterval) {
		this.statsInterval = statsInterval;
	}
	
	public static void main(String... args) {
		SenderManager sm = new SenderPool();
		Map<String, String> params = new HashMap<String, String>();
		params.put("group", "224.0.0.1");
		params.put("port", "1234");
		params.put("ttl", "127");
		params.put("ninf", "127.0.0.1");
		params.put("pps", "1000");
		params.put("psize", "300");
		params.put("payload", "Hallo Welt");
		params.put("ptype", "spam");
		for(int i=0; i<15; i++) {
			sm.create(params).activate();
		}
		try { Thread.sleep(30*1000); } catch(Exception e) {}
		sm.killAll();
	}

}
