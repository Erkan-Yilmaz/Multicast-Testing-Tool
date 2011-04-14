package com.spam.mctool.model;

import java.util.HashMap;
import java.util.Map;
import com.spam.mctool.intermediates.ReceiverDataChangedEvent;
import com.spam.mctool.intermediates.SenderDataChangedEvent;

public class ModelTest implements ReceiverDataChangeListener, SenderDataChangeListener {
	
	public static void main(String... args) throws InterruptedException {
		SenderManager sm = new SenderPool();
		ReceiverManager rm = new ReceiverPool();
		ModelTest mt = new ModelTest();
		
		Sender s = null;
		Map<String, String> sp = new HashMap<String, String>();
		sp.put("group", "224.0.0.1");
		sp.put("ninf", "127.0.0.1");
		sp.put("port", "8888");
		sp.put("ptype", "spam");
		sp.put("psize", "1000");
		sp.put("abeh", "lazy");
		sp.put("pps", "1000");
		sp.put("ttl", "127");
		sp.put("payload", "SPAM FOR THE WORLD");
		for(int i=0; i<2; i++) {
			sp.put("group", "224.0.0.1");
			s = sm.create(sp);
			s.addSenderDataChangeListener(mt);
			s.activate();
		}
		
		Map<String, String> rp = new HashMap<String, String>();
		rp.put("ninf", "127.0.0.1");
		rp.put("group", "224.0.0.1");
		rp.put("port", "8888");
		rp.put("abeh", "default");
		ReceiverGroup r=null;
		for(int i=0; i<1; i++) {
			rp.put("group", "224.0.0.1");
			r = rm.create(rp);
			r.addReceiverDataChangeListener(mt);
			r.activate();
		}
		
		Thread.sleep(5000);
		s.deactivate();
		
	}

	public void dataChanged(ReceiverDataChangedEvent e) {
		System.out.println(e.getSource());
		for(Receiver r : e.getReceiverList()) {
			System.out.println(r);
		}
	}

	public void dataChanged(SenderDataChangedEvent e) {
		System.out.println(e.getSource());
	}
	
}
