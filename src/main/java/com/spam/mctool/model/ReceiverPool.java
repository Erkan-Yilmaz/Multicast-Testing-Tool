package com.spam.mctool.model;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ReceiverPool implements ReceiverManager {
	
	// internals
	private int threadPoolSize = 15;
	private int statsInterval = 1000;
	private ScheduledThreadPoolExecutor stpe;
	private List<ReceiverGroup> receiverGroups;
	
	public ReceiverPool() {
		stpe = new ScheduledThreadPoolExecutor(threadPoolSize);
		receiverGroups = new LinkedList<ReceiverGroup>();
	}

	public void addReceiverAddedOrRemovedListener(
			ReceiverAddedOrRemovedListener l) {
		// TODO Auto-generated method stub

	}

	public ReceiverGroup create(Map<String, String> params) {
		InetAddress group;
		int port;
		NetworkInterface ninf;
		MulticastStream.AnalyzingBehaviour abeh;
		
		try {
			// try to create a inet adress group
			group = InetAddress.getByName(
				params.get("group")
			);
			// try to parse the port
			port = new Integer(
				params.get("port")
			);
			// try to parse Network Interface
			ninf = NetworkInterface.getByInetAddress(
				InetAddress.getByName(
					params.get("ninf")
				)
			);
			// try to parse analyzing behavior
			abeh = MulticastStream.AnalyzingBehaviour.getByIdentifier(
				params.get("abeh")	
			);
		} catch(Exception e) {
			throw new IllegalArgumentException();
		}
		
		ReceiverGroup rec = new ReceiverGroup(stpe);
		rec.setGroup(group);
		rec.setPort(port);
		rec.setNetworkInterface(ninf);
		rec.setAnalyzingBehaviour(abeh);
		rec.setStatsInterval(statsInterval);
		receiverGroups.add(rec);
                
		return rec;
	}

	public Collection<ReceiverGroup> getReceiverGroups() {
		return receiverGroups;
	}

	public void killAll() {
		stpe.shutdown();
	}

	public void remove(ReceiverGroup receiver) {
		// TODO Auto-generated method stub

	}

	public void removeReceiverAddedOrRemovedListener(
			ReceiverAddedOrRemovedListener l) {
		// TODO Auto-generated method stub

	}

}
