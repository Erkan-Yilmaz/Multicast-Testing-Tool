package com.spam.mctool.model;

import org.junit.*;
import static org.junit.Assert.*;

class TestEvent {
	public String name;
}

class TestListener {
	public void changed(TestEvent e) {
		name = e.name;		
	}
	
	public String name;
}

/**
 * @author konne
 * 
 * Tests the Signal implementation
 */
public class SignalTest {
	@Test
	public void test_listeners() {
		Signal<TestListener> s = new Signal<TestListener>() {
			@Override
			protected void fire(Object event, TestListener l) {
				l.changed((TestEvent)event);
			}
		};
		TestListener a = new TestListener();
		TestListener b = new TestListener();
		
		s.addListener(a);		
		TestEvent e = new TestEvent();
		e.name = "Keks";
		s.fire(e);
		assertEquals("Keks", a.name);
		
		s.addListener(b);
		e.name = "Milch";
		s.fire(e);		
		assertEquals("Milch", a.name);
		assertEquals("Milch", b.name);
	}
	
}
