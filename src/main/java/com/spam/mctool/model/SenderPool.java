package com.spam.mctool.model;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.spam.mctool.controller.Controller;
import com.spam.mctool.controller.ErrorEvent;
import com.spam.mctool.controller.ErrorEventManager;
import com.spam.mctool.intermediates.OverallSenderStatisticsUpdatedEvent;
import com.spam.mctool.intermediates.SenderAddedOrRemovedEvent;

public class SenderPool implements SenderManager {
	// internals
	private ScheduledThreadPoolExecutor stfe;
	private Set<Sender> senders = new HashSet<Sender>();
	private List<SenderAddedOrRemovedListener> saorl;
	private List<OverallSenderStatisticsUpdatedListener> statsListeners;
	private ErrorEventManager eMan;
	private int threadPoolSize = 5;
	// statistics
	private Runnable analyzer;
	private int statsInterval = 1000;
	private Byte statsLock = new Byte((byte)0);
	private long overallSentPackets;
	private long overallSendingPPS;
	
	public SenderPool() {
		this.eMan = Controller.getController();
		this.saorl = new LinkedList<SenderAddedOrRemovedListener>();
		this.statsListeners = new LinkedList<OverallSenderStatisticsUpdatedListener>();
		analyzer = new OverallSenderStatisticAnalyzer();
		this.stfe = new ScheduledThreadPoolExecutor(threadPoolSize);
		stfe.scheduleAtFixedRate(analyzer, statsInterval, statsInterval, TimeUnit.MILLISECONDS);
	}

	public Sender create(Map<String, String> params) throws IllegalArgumentException {
		Sender sender;
		InetAddress group;
		Integer port = 0;
		int ttl = 0;
		int pps = 0;
		int psize = 0;
		String payload;
		Sender.PacketType ptype;
		NetworkInterface ninf = null;
		MulticastStream.AnalyzingBehaviour abeh;
		
		// handle the group
		group = MulticastStream.getMulticastGroupByName(params.get("group"));
		if(null == group) {
			eMan.reportErrorEvent(
				new ErrorEvent(4, "Model.SenderPool.create.InvalidMulticastGroup.text", "")
			);
			return null;
		}
		
		// handle the network interface
		ninf = MulticastStream.getNetworkInterfaceByAddress(params.get("ninf"));
		if(null == ninf) {
			eMan.reportErrorEvent(
				new ErrorEvent(4, "Model.SenderPool.create.FatalNetworkError.text", "")
			);
			return null;
		}
		
		// handle the port
		port = MulticastStream.getPortByName(params.get("port"));
		if(null == port) {
			eMan.reportErrorEvent(
				new ErrorEvent(4, "Model.SenderPool.create.InvalidPort.text", "")
			);
			return null;
		}
		
		// handle time to live
		try {
			ttl = new Integer(params.get("ttl"));
			if((ttl<0) || (ttl>255)) {
				throw new Exception();
			}
		} catch(Exception e) {
			eMan.reportErrorEvent(
				new ErrorEvent(4, "Model.SenderPool.create.InvalidTtl.text", "")
			);
			return null;
		}
		
		// handle packet rate
		try {
			pps = new Integer(params.get("pps"));
			if((pps<=0) || (pps>1000)) {
				throw new Exception();
			}
		} catch(Exception e) {
			eMan.reportErrorEvent(
				new ErrorEvent(4, "Model.SenderPool.create.InvalidPps.text", "")
			);
			return null;
		}
		
		// handle packet size
		try {
			psize = new Integer(params.get("psize"));
			if((psize<0) || (psize>9000)) {
				throw new Exception();
			}
		} catch(Exception e) {
			eMan.reportErrorEvent(
				new ErrorEvent(4, "Model.SenderPool.create.InvalidPacketSize.text", "Corrected to: "+psize)
			);
		}
		
		// handle payload
		payload = params.get("payload");
		
		// handle packet type
		ptype = MulticastStream.PacketType.getByIdentifier(
			params.get("ptype")
		);
		
		// handle analyzing behaviour
		abeh = MulticastStream.AnalyzingBehaviour.getByIdentifier(
			params.get("abeh")
		);
		
		sender = new Sender(this.stfe);
		sender.setGroup(group);
		sender.setPort(port);
		sender.setTtl(ttl);
		sender.setSenderConfiguredPacketRate(pps);
		sender.setPacketSize(psize);
		sender.setPayloadFromString(payload);
		sender.setpType(ptype);
		sender.setNetworkInterface(ninf);
		sender.setAnalyzingBehaviour(abeh);
		sender.setStatsInterval(statsInterval);
		
		this.senders.add(sender);
		this.fireSenderAddedEvent(sender);
		return sender;
	}

	public void remove(Sender sender) {
		sender.deactivate();
		senders.remove(sender);
		this.fireSenderRemovedEvent(sender);
	}

	public Collection<Sender> getSenders() {
		return new HashSet<Sender>(senders);
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
	
	private class OverallSenderStatisticAnalyzer implements Runnable {
		@Override
		public void run() {
			int senderCount = senders.size();
			if(senderCount > 0) {
				synchronized(statsLock) {
					overallSentPackets = 0;
					overallSendingPPS = 0;
					for(Sender s : senders) {
						overallSentPackets += s.getSentPacketCount();
						overallSendingPPS += s.getAvgPPS();
					}
				}
			}
			OverallSenderStatisticsUpdatedEvent e = new OverallSenderStatisticsUpdatedEvent(SenderPool.this);
			for(OverallSenderStatisticsUpdatedListener l : statsListeners) {
				l.overallSenderStatisticsUpdated(e);
			}
		}
	}

	@Override
	public long getOverallSentPackets() {
		synchronized(statsLock) {
			return overallSentPackets;
		}
	}

	@Override
	public long getOverallSentPPS() {
		synchronized(statsLock) {
			return overallSendingPPS;
		}
	}

	@Override
	public void addOverallSenderStatisticsUpdatedListener(
			OverallSenderStatisticsUpdatedListener l) {
		statsListeners.add(l);
	}

	@Override
	public void removeOverallSenderStatisticsUpdatedListener(
			OverallSenderStatisticsUpdatedListener l) {
		statsListeners.remove(l);
	}

}
