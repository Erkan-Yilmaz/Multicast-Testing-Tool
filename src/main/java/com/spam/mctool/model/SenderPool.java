package com.spam.mctool.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.spam.mctool.intermediates.OverallSenderStatisticsUpdatedEvent;
import com.spam.mctool.intermediates.SenderAddedOrRemovedEvent;
import com.spam.mctool.model.MulticastStream.AnalyzingBehaviour;
import com.spam.mctool.model.MulticastStream.PacketType;

public class SenderPool implements SenderManager {
	// internals
	private ScheduledThreadPoolExecutor stfe;
	private Set<Sender> senders = new HashSet<Sender>();
	private List<SenderAddedOrRemovedListener> saorl;
	private List<OverallSenderStatisticsUpdatedListener> statsListeners;
	private int threadPoolSize = 5;
	// statistics
	private Runnable analyzer;
	private int statsInterval = 1000;
	private Byte statsLock = new Byte((byte)0);
	private long overallSentPackets;
	private long overallSendingPPS;
	
	public SenderPool() {
		this.saorl = new LinkedList<SenderAddedOrRemovedListener>();
		this.statsListeners = new LinkedList<OverallSenderStatisticsUpdatedListener>();
		analyzer = new OverallSenderStatisticAnalyzer();
		this.stfe = new ScheduledThreadPoolExecutor(threadPoolSize);
		stfe.scheduleAtFixedRate(analyzer, statsInterval, statsInterval, TimeUnit.MILLISECONDS);
	}

	public Sender create(Map<String, String> params) throws IllegalArgumentException {
		boolean checksOk = true;
		Sender sender = new Sender(this.stfe);
		
		checksOk &= sender.setGroup(
			params.get("group")
		);
		checksOk &= sender.setPort(
			params.get("port")
		);
		checksOk &= sender.setNetworkInterface(
			params.get("ninf")
		);		
		checksOk &= sender.setSenderConfiguredPacketRate(
			params.get("pps")
		);
		checksOk &= sender.setTtl(
			params.get("ttl")
		);
		checksOk &= sender.setPacketSize(
			params.get("psize")
		);
		
		sender.setAnalyzingBehaviour(
			AnalyzingBehaviour.getByIdentifier(
				params.get("abeh")
			)
		);
		
		sender.setpType(
			PacketType.getByIdentifier(
				params.get("ptype")
			)
		);
		
		sender.setPayloadFromString(
			params.get("payload")
		);
		
		sender.setStatsInterval(statsInterval);
		
		if(checksOk) {
			this.senders.add(sender);
			this.fireSenderAddedEvent(sender);
			return sender;
		} else {
			return null;
		}
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
