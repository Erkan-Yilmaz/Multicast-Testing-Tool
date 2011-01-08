package com.spam.mctool.controller;

import org.junit.*;
import static org.junit.Assert.*;

public class ControllerTest {
	
	@Test
	public void test_getHelloMsg() {
		assertEquals("getHelloMsg() does not return 'Hello Team 1 ;)'.", "Hello Team 1 ;)", new Controller().getHelloMsg());
	}
	
}
