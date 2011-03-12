package com.spam.mctool.model;

import java.util.HashMap;
import java.util.Map;

public class ModelTest {
	
	public static void main(String... args) {
		SenderManager sm = new SenderPool();
		ReceiverManager rm = new ReceiverPool();
		
		Sender s = null;
		Map<String, String> sp = new HashMap<String, String>();
		sp.put("group", "224.0.0.1");
		sp.put("ninf", "127.0.0.1");
		sp.put("port", "8888");
		sp.put("ptype", "spam");
		sp.put("psize", "9000");
		sp.put("abeh", "default");
		sp.put("pps", "100");
		sp.put("ttl", "127");
		sp.put("payload", "SPAM FOR THE WORLD");
		for(int i=0; i<1; i++) {
			sp.put("group", "224.0.0.1");
			s = sm.create(sp);
			s.activate();
		}
		
		Map<String, String> rp = new HashMap<String, String>();
		rp.put("ninf", "127.0.0.1");
		rp.put("group", "224.0.0.1");
		rp.put("port", "8888");
		rp.put("abeh", "lazy");
		for(int i=0; i<1; i++) {
			rp.put("group", "224.0.0.1");
			rm.create(rp).activate();
		}
		
		try {
			Thread.sleep(3*1000);
			s.deactivate();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(3*1000);
			s.activate();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(2*1000);
			sm.killAll();
			rm.killAll();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
