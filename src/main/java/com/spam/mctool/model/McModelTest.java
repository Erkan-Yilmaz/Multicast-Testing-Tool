package com.spam.mctool.model;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.spam.mctool.intermediates.ReceiverDataChangedEvent;
import com.spam.mctool.intermediates.SenderDataChangedEvent;

public class McModelTest implements SenderDataChangeListener, ReceiverDataChangeListener {
	
	public static void main(String... args) {
		McModelTest mctest = new McModelTest();
		String group = "224.0.0.1";
		NetworkInterface netint = null;
		try {
			netint = NetworkInterface.getByName("lo");
		} catch (SocketException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		
		//sender
		Sender s = new Sender();
		s.setData(new String("Hallo Welt").getBytes());
		s.setNetworkInterface(netint);
		s.setSenderConfiguredPacketRate(100);
		s.setPort(8888);
		try {
			s.setGroupByString(group);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		s.addSenderDataChangeListener(mctest);
		s.activate();
		
		//receiver
		Receiver r = new Receiver();
		r.setNetworkInterface(netint);
		try {
			r.setGroupByString(group);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		r.setPort(8888);
		r.setPacketSize(100);
		r.addReceiverDataChangeListener(mctest);
		r.activate();
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.deactivate();
		r.deactivate();
	}

	public void dataChanged(SenderDataChangedEvent e) {
		Sender r = e.getSource();
		String msg = "Sender:\n----------------------\n" +
				"Sending Rates (MIN|AVG|MAX) pps: "+r.getMinPacketRate()+"|"+r.getAvgPacketRate()+"|"+r.getMaxPacketRate()+"\n";
		System.out.println(msg);
	}

	public void dataChanged(ReceiverDataChangedEvent e) {
		Receiver r = e.getSource();
		String msg = "Receiver:\n----------------------\n" +
				"Traversal (MIN|AVG|MAX) ms: "+r.getMinTraversal()+"|"+r.getAvgTraversal()+"|"+r.getMaxTraversal()+"\n" +
				"Receiving Rates (MIN|AVG|MAX) pps: "+r.getMinPacketRate()+"|"+r.getAvgPacketRate()+"|"+r.getMaxPacketRate()+"\n"+
				"Sender (MRate|CRate|ID): "+r.getSenderMeasuredRate()+"|"+r.getSenderConfiguredRate()+"|"+r.getSenderId()+"\n"+
				"Totals (Sender|Receiver): "+r.getSenderPacketCount()+"|"+r.getReceivedPacketCount()+"\n"+
				"Other (errors|losses): "+r.getFaultyPackets()+"|"+r.getLostPackets()+"\n"+
				"Payload: "+r.getSenderPayload()+"\n";
		System.out.println(msg);
	}
	
}
