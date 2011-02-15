package com.spam.mctool.model;

import org.junit.*;
import static org.junit.Assert.*;

class Event {
	public String name;
}

class Listener {
	public void changed(Event e) {
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
		Signal<Listener> s = new Signal<Listener>() {
			@Override
			protected void fire(Object event, Listener l) {
				l.changed((Event)event);
			}
		};
		Listener a = new Listener();
		Listener b = new Listener();
		
		s.addListener(a);		
		Event e = new Event();
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
