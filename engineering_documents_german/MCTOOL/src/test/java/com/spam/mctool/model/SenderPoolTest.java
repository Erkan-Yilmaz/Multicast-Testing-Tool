package com.spam.mctool.model;


import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

/**
 * TC: M04
 * Ensures correct function of SenderPool
 * @author Jeffrey Jedele
 */
public class SenderPoolTest {

	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testKeyPoints() {
		SenderPool sp = new SenderPool();
		HashMap<String, String> correctParams = new HashMap<String, String>();
		HashMap<String, String> falseParams = new HashMap<String, String>();
		
		// only one correct and false configuration, single
		// values are checked in MulticastStreamTest
		
		// correct
		correctParams.put("ninf", "127.0.0.1");
		correctParams.put("group", "224.0.0.1");
		correctParams.put("abeh", "default");
		correctParams.put("port", "65000");
		correctParams.put("psize", "1000");
		correctParams.put("ptype", "spam");
		correctParams.put("pps", "1000");
		correctParams.put("ttl", "34");
		correctParams.put("payload", "Bla");
		
		Sender s = sp.create(correctParams);
		if(s == null) {
			fail("SenderPool returned null although correct values");
		}
		
		// invalid
		falseParams.put("ninf", "127.0.0.1");
		falseParams.put("group", "224.0.0.1");
		falseParams.put("abeh", "default");
		falseParams.put("port", "-1"); // error
		falseParams.put("psize", "1000");
		falseParams.put("ptype", "spam");
		falseParams.put("pps", "1000");
		falseParams.put("ttl", "34");
		falseParams.put("payload", "Bla");
		
		Sender fs = sp.create(falseParams);
		if(fs != null) {
			fail("SenderPool did create Sender with invalid values");
		}
		
		// is receiver group stored by pool?
		if(!sp.getSenders().contains(s)) {
			fail("Created Sender not stored in Pool");
		}
		
		// test deletion
		sp.remove(s);
		if(sp.getSenders().contains(s)) {
			fail("Created Sender not deleted from Pool");
		}
	}

}
