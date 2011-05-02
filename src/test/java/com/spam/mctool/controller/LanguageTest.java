package com.spam.mctool.controller;

import static org.junit.Assert.*;

import org.junit.Test;

public class LanguageTest implements LanguageChangeListener{
	private int x;

	@Test
	public void testAddLanguageChangeListener() {
		//Fetch the controller
		Controller c = Controller.getController();
		//set x to 0
		this.x = 0;
		//report a Language Change
		c.reportLanguageChange();
		//nothing should have happend to x
		assertEquals(x, 0);
		//Register as LanguageChangeListener
		c.addLanguageChangeListener(this);
		//set x to 0
		this.x = 0;
		//report a Language Change
		c.reportLanguageChange();
		//x should have changed now
		assertEquals(x, 1);
		//try to add null
		try{
			c.addLanguageChangeListener(this);
		}
		catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	@Test
	public void testRemoveProfileChangeListenerLanguageChangeListener() {
		//Fetch the controller
		Controller c = Controller.getController();
		//set x to 0
		this.x = 0;
		//Register as LanguageChangeListener
		c.addLanguageChangeListener(this);
		//set x to 0
		this.x = 0;
		//report a Language Change
		c.reportLanguageChange();
		//x should have changed now
		assertEquals(x, 1);
		//remove as listener
		c.removeLanguageChangeListener(this);
		//set x to 0
		this.x = 0;
		//report a Language Change
		c.reportLanguageChange();
		//x should not have changed
		assertEquals(x, 0);
		//try to remove null
		try{
			c.removeLanguageChangeListener(this);
		}
		catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	@Override
	public void languageChanged() {
		this.x = 1;
	}
}
