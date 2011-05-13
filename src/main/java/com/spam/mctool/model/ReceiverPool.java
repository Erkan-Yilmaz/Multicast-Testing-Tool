package com.spam.mctool.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.spam.mctool.intermediates.OverallReceiverStatisticsUpdatedEvent;
import com.spam.mctool.intermediates.ReceiverAddedOrRemovedEvent;
import com.spam.mctool.model.MulticastStream.AnalyzingBehaviour;

public class ReceiverPool implements ReceiverManager {
	
	// internals
	private int threadPoolSize = 5;
	private int statsInterval = 1000;
	private ScheduledThreadPoolExecutor stpe;
	private List<ReceiverGroup> receiverGroups;
	private List<ReceiverAddedOrRemovedListener> raorListeners;
	private List<OverallReceiverStatisticsUpdatedListener> statsListeners;
	// statistics
	private Runnable analyzer;
	private long overallStatsIntervall = 1000;
	private Byte statsLock = new Byte((byte) 0);
	private long overallReceivedPackets;
	private long overallReceivedPPS;
	private long overallLostPackets;
	private long overallFaultyPackets;
	
	/**
	 * Creates a new receiver pool.
	 */
	public ReceiverPool() {
		stpe = new ScheduledThreadPoolExecutor(threadPoolSize);
		receiverGroups = new LinkedList<ReceiverGroup>();
		raorListeners = new LinkedList<ReceiverAddedOrRemovedListener>();
		analyzer = new ReceiverSummaryAnalyzer();
		statsListeners = new LinkedList<OverallReceiverStatisticsUpdatedListener>();
		stpe.scheduleAtFixedRate(analyzer, overallStatsIntervall, overallStatsIntervall, TimeUnit.MILLISECONDS);
	}

	public ReceiverGroup create(Map<String, String> params) {
		boolean checksOk = true;
		ReceiverGroup rec = new ReceiverGroup(stpe);
		checksOk &= rec.setGroup(
			params.get("group")
		);
		checksOk &= rec.setPort(
			params.get("port")
		);
		checksOk &= rec.setNetworkInterface(
			params.get("ninf")
		);
		rec.setAnalyzingBehaviour(
			AnalyzingBehaviour.getByIdentifier(
				params.get("abeh")
			)
		);
		rec.setStatsInterval(statsInterval);
	
		if(checksOk) {
			receiverGroups.add(rec);
			fireReceiverAddedEvent(rec);
			return rec;
		} else {
			return null;
		}
	}

	public Collection<ReceiverGroup> getReceiverGroups() {
		return new HashSet<ReceiverGroup>(receiverGroups);
	}

	public void killAll() {
		stpe.shutdown();
	}

	public void remove(ReceiverGroup receiver) {
		receiver.deactivate();
		receiverGroups.remove(receiver);
		fireReceiverRemovedEvent(receiver);
	}
	
	public void addReceiverAddedOrRemovedListener(
			ReceiverAddedOrRemovedListener l) {
		raorListeners.add(l);
	}

	public void removeReceiverAddedOrRemovedListener(
			ReceiverAddedOrRemovedListener l) {
		raorListeners.remove(l);
	}
	
	// used to inform interested classes about added receivers
	private void fireReceiverAddedEvent(ReceiverGroup r) {
		ReceiverAddedOrRemovedEvent e = new ReceiverAddedOrRemovedEvent(r);
		for(ReceiverAddedOrRemovedListener l : raorListeners) {
			l.receiverGroupAdded(e);
		}
	}
	
	// used to inform interested classes about removed receivers
	private void fireReceiverRemovedEvent(ReceiverGroup r) {
		ReceiverAddedOrRemovedEvent e = new ReceiverAddedOrRemovedEvent(r);
		for(ReceiverAddedOrRemovedListener l : raorListeners) {
			l.receiverGroupRemoved(e);
		}
	}
	
	private class ReceiverSummaryAnalyzer implements Runnable {
		@Override
		public void run() {
			int groupCount = receiverGroups.size();
			if(groupCount > 0) {
				synchronized(statsLock) {
					overallReceivedPackets = 0;
					overallReceivedPPS = 0;
					overallLostPackets = 0;
					overallFaultyPackets = 0;
					for(ReceiverGroup r : receiverGroups) {
						if (r.isActive()) {
							overallReceivedPackets += r.getReceivedPackets();
							overallReceivedPPS += r.getTotalPPS();
							overallLostPackets += r.getLostPackets();
							overallFaultyPackets += r.getFaultyPackets();
						}
					}
				}
			} else {
				synchronized(statsLock) {
					overallReceivedPackets = 0;
					overallReceivedPPS = 0;
					overallLostPackets = 0;
					overallFaultyPackets = 0;
				}
			}
			OverallReceiverStatisticsUpdatedEvent e = new OverallReceiverStatisticsUpdatedEvent(ReceiverPool.this);
			for(OverallReceiverStatisticsUpdatedListener l : statsListeners) {
				l.overallReceiverStatisticsUpdated(e);
			}
		}
	}

	/**
	 * @return sum of packets received by all active senders
	 */
	public long getOverallReceivedPackets() {
		synchronized(statsLock) {
			return overallReceivedPackets;
		}
	}

	/**
	 * @return sum of receiving packet rates of all active senders
	 */
	public long getOverallReceivedPPS() {
		synchronized(statsLock) {
			return overallReceivedPPS;
		}
	}

	/**
	 * @return sum of lost packets of all active senders
	 */
	public long getOverallLostPackets() {
		synchronized(statsLock) {
			return overallLostPackets;
		}
	}
	
	/**
	 * @return percentual packet losing rate
	 */
	public double getLostPacketsPermille() {
		synchronized(statsLock) {
			return (double)overallLostPackets/(double)overallReceivedPackets*1000.0;
		}
	}

	/**
	 * @return sum of faulty packets of all active senders
	 */
	public long getOverallFaultyPackets() {
		synchronized(statsLock) {
			return overallFaultyPackets;
		}
	}

	@Override
	public void addOverallReceiverStatisticsUpdatedListener(
			OverallReceiverStatisticsUpdatedListener l) {
		statsListeners.add(l);
	}

	@Override
	public void removeOverallReceiverStatisticsUpdatedListener(
			OverallReceiverStatisticsUpdatedListener l) {
		statsListeners.remove(l);
	}
	
	@Override
	public void setThreadPoolSize(int size) {
		threadPoolSize = size;
		stpe.setCorePoolSize(threadPoolSize);
	}

}
