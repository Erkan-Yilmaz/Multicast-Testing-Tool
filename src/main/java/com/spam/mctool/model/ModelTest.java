package com.spam.mctool.model;

import java.util.HashMap;
import java.util.Map;

public class ModelTest {
	
	public static void main(String... args) {
		SenderManager sm = new SenderPool();
		ReceiverManager rm = new ReceiverPool();
		
		Map<String, String> sp = new HashMap<String, String>();
		sp.put("group", "224.0.0.1");
		sp.put("ninf", "127.0.0.1");
		sp.put("port", "8888");
		sp.put("ptype", "spam");
		sp.put("psize", "1000");
		sp.put("abeh", "default");
		sp.put("pps", "333");
		sp.put("ttl", "127");
		sp.put("payload", "SPAM FOR THE WORLD");
		for(int i=0; i<1; i++) {
			sp.put("group", "224.0.0."+i);
			sm.create(sp).activate();
		}
		
		Map<String, String> rp = new HashMap<String, String>();
		rp.put("ninf", "127.0.0.1");
		rp.put("group", "224.0.0.1");
		rp.put("port", "8888");
		rp.put("abeh", "default");
		for(int i=0; i<1; i++) {
			rp.put("group", "224.0.0."+i);
			rm.create(rp).activate();
		}
	}
	
}
