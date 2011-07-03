package com.spam.mctool.model;


import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

/**
 * TC: M03
 * Ensures correct function of ReceiverPool
 * @author Jeffrey Jedele
 */
public class ReceiverPoolTest {

	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testKeyPoints() {
		ReceiverPool rp = new ReceiverPool();
		HashMap<String, String> correctParams = new HashMap<String, String>();
		HashMap<String, String> falseParams = new HashMap<String, String>();
		
		// only one correct and false configuration, single
		// values are checked in MulticastStreamTest
		
		// correct
		correctParams.put("ninf", "127.0.0.1");
		correctParams.put("group", "224.0.0.1");
		correctParams.put("abeh", "default");
		correctParams.put("port", "65000");
		
		ReceiverGroup r = rp.create(correctParams);
		if(r == null) {
			fail("ReceiverPool returned null although correct values");
		}
		
		// invalid
		falseParams.put("ninf", "127.0.0.1");
		falseParams.put("group", "224.0.0.1");
		falseParams.put("abeh", "default");
		falseParams.put("port", "-1"); // error
		
		ReceiverGroup fr = rp.create(falseParams);
		if(fr != null) {
			fail("ReceiverPool did create Receiver with invalid values");
		}
		
		// is receiver group stored by pool?
		if(!rp.getReceiverGroups().contains(r)) {
			fail("Created Receiver not stored in Pool");
		}
		
		// test deletion
		rp.remove(r);
		if(rp.getReceiverGroups().contains(r)) {
			fail("Created Receiver not deleted from Pool");
		}
	}

}
