package com.spam.mctool.view;

import static org.junit.Assert.*;

import java.awt.List;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import com.spam.mctool.controller.Controller;
import com.spam.mctool.model.ReceiverGroup;

public class CommandLineViewTest {
	
	CommandLineView cli = null;
	Controller c = null;
	
	@Test
	public void initTest(){
		cli = new CommandLineView();
		assertTrue(cli != null);
		
		c = Controller.getController();
		String initString[] = {"-nogui"};
		c.init(initString);
		assertTrue(c != null);
		
		cli.init(c);	
		
	}
	
	@Test
	public void profileCangedTest(){
		cli = new CommandLineView();
		assertTrue(cli != null);
		
		c = Controller.getController();
		String initString[] = {"-nogui"};
		c.init(initString);
		assertTrue(c != null);
		
		cli.init(c);
		c.setCurrentProfile(null);
		
	}
	
	@Test
	public void receiverGroupAddedTest() {
		cli = new CommandLineView();
		assertTrue(cli != null);
		
		c = Controller.getController();
		String initString[] = {"-nogui"};
		c.init(initString);
		assertTrue(c != null);
		
		
		
		Map <String,String> prop = new HashMap<String,String>();
		
		prop.put("group", "224.0.0.1");
		prop.put("port", "12345");
		prop.put("ninf","127.0.0.1");
		prop.put("abeh","Default");
		
		cli.init(c);
		
		c.addReceiverGroup(prop);
		
		
		
	}
	
public void receiverGroupRemovedTest() {
		cli = new CommandLineView();
		assertTrue(cli != null);
	
		c = Controller.getController();
		String initString[] = {"-nogui"};
		c.init(initString);
		assertTrue(c != null);
	
		cli.init(c);
		
		Map <String,String> prop = new HashMap<String,String>();
		
		prop.put("group", "224.0.0.1");
		prop.put("port", "12345");
		prop.put("ninf","127.0.0.1");
		prop.put("abeh","Default");
		
		ReceiverGroup obj = c.addReceiverGroup(prop);
		
			
	}
}
