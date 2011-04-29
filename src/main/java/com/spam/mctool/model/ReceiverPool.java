package com.spam.mctool.model;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.spam.mctool.intermediates.OverallReceiverStatisticsUpdatedEvent;
import com.spam.mctool.intermediates.ReceiverAddedOrRemovedEvent;
import com.spam.mctool.intermediates.ReceiverCreationException;

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
		stpe.scheduleAtFixedRate(analyzer, overallStatsIntervall, overallStatsIntervall, TimeUnit.MILLISECONDS);
	}

	public ReceiverGroup create(Map<String, String> params) {
		InetAddress group;
		int port;
		NetworkInterface ninf;
		MulticastStream.AnalyzingBehaviour abeh;
		ReceiverCreationException.ErrorType errorType = null;
		
		try {
			// try to create a inet adress group
			errorType = ReceiverCreationException.ErrorType.GROUP;
			group = InetAddress.getByName(
				params.get("group")
			);
			if(!group.isMulticastAddress()) {
				// if address is not in multicast range
				throw new Exception();
			}
			// try to parse the port
			errorType = ReceiverCreationException.ErrorType.PORT;
			port = new Integer(
				params.get("port")
			);
			if(port<0 && port>65535) {
				throw new Exception();
			}
			// try to parse Network Interface
			errorType = ReceiverCreationException.ErrorType.NETWORKINTERFACE;
			ninf = NetworkInterface.getByInetAddress(
				InetAddress.getByName(
					params.get("ninf")
				)
			);
			// try to parse analyzing behavior
			errorType = ReceiverCreationException.ErrorType.ANALYZINGBEHAVIOR;
			if(params.containsKey("abeh")) {
				abeh = MulticastStream.AnalyzingBehaviour.getByIdentifier(
						params.get("abeh").toLowerCase()
				);
			} else {
				abeh = MulticastStream.AnalyzingBehaviour.DEFAULT;
			}
		} catch(Exception e) {
			System.out.println(errorType);
			throw new ReceiverCreationException(errorType);
		}
		
		ReceiverGroup rec = new ReceiverGroup(stpe);
		rec.setGroup(group);
		rec.setPort(port);
		rec.setNetworkInterface(ninf);
		rec.setAnalyzingBehaviour(abeh);
		rec.setStatsInterval(statsInterval);
		receiverGroups.add(rec);
		fireReceiverAddedEvent(rec);
	
		return rec;
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
			synchronized(statsLock) {
				int groupCount = receiverGroups.size();
				if(groupCount > 0) {
					overallReceivedPackets = 0;
					overallReceivedPPS = 0;
					overallLostPackets = 0;
					overallFaultyPackets = 0;
					for(ReceiverGroup r : receiverGroups) {
						overallReceivedPackets += r.getReceivedPackets();
						overallReceivedPPS += r.getAvgPPS();
						overallLostPackets += r.getLostPackets();
						overallFaultyPackets += r.getFaultyPackets();
					}
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
	 * @return sum of faulty packets of all active senders
	 */
	public long getOverallFaultyPackets() {
		synchronized(statsLock) {
			return overallFaultyPackets;
		}
	}

	@Override
	public void addOverallReceiverStatisticsUpdateListener(
			OverallReceiverStatisticsUpdatedListener l) {
		statsListeners.add(l);
	}

	@Override
	public void removeOverallReceiverStatisticsUpdateListener(
			OverallReceiverStatisticsUpdatedListener l) {
		statsListeners.remove(l);
	}

}
