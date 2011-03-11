package com.spam.mctool.model;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ReceiverPool implements ReceiverManager {
	
	int threadPoolSize = 15;
	int statsInterval = 1000;
	ScheduledThreadPoolExecutor stpe;
	
	public ReceiverPool() {
		stpe = new ScheduledThreadPoolExecutor(threadPoolSize);
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
	
		return rec;
	}

	public Collection<ReceiverGroup> getReceiver() {
		// TODO Auto-generated method stub
		return null;
	}

	public void killAll() {
		// TODO Auto-generated method stub

	}

	public void remove(ReceiverGroup receiver) {
		// TODO Auto-generated method stub

	}

	public void removeReceiverAddedOrRemovedListener(
			ReceiverAddedOrRemovedListener l) {
		// TODO Auto-generated method stub

	}

}
