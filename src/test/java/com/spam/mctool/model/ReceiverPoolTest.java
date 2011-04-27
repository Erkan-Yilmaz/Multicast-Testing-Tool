package com.spam.mctool.model;


import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.spam.mctool.intermediates.ReceiverCreationException;

public class ReceiverPoolTest {

	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testIfParametersAreCheckedCorrectly() {
		ReceiverPool rp = new ReceiverPool();
		HashMap<String, String> params = new HashMap<String, String>();
		// test if wrong ipv4 groups are recognized
		try {
			params.put("group", "223.255.255.255");
			rp.create(params);
		} catch(ReceiverCreationException e) {
			assertEquals("wrong ipv4 multicast address not recognized (lower bound)", ReceiverCreationException.ErrorType.GROUP, e.getErrorType());
		}
		try {
			params.put("group", "240.0.0.0");
			rp.create(params);
		} catch(ReceiverCreationException e) {
			assertEquals("wrong ipv4 multicast address not recognized (upper bound)", ReceiverCreationException.ErrorType.GROUP, e.getErrorType());
		}
		// test if wrong ipv6 groups are recognized
		try {
			params.put("group", "223.255.255.255");
			rp.create(params);
		} catch(ReceiverCreationException e) {
			assertEquals("wrong ipv6 multicast address not recognized (lower bound)", ReceiverCreationException.ErrorType.GROUP, e.getErrorType());
		}
		try {
			params.put("group", "240.0.0.0");
			rp.create(params);
		} catch(ReceiverCreationException e) {
			assertEquals("wrong ipv6 multicast address not recognized (upper bound)", ReceiverCreationException.ErrorType.GROUP, e.getErrorType());
		}
	}

}
