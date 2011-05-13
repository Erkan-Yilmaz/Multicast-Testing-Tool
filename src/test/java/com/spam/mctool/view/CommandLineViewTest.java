package com.spam.mctool.view;

import static org.junit.Assert.*;

import java.awt.List;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import com.spam.mctool.controller.Controller;
import com.spam.mctool.controller.ErrorEvent;
import com.spam.mctool.controller.ErrorEventManager;
import com.spam.mctool.model.ReceiverGroup;
import com.spam.mctool.model.Sender;

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

	@Test
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
		Collection<ReceiverGroup> coll =  new ArrayList<ReceiverGroup>();
		coll.add(obj);
		
		c.removeStreams(coll);			
	}


	@Test
	public void senderAddedTest(){
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
		prop.put("ptype","spam");
		prop.put("payload","Default");
		prop.put("psize","32");
		prop.put("pps","10");
		prop.put("ttl","32");
		
		c.addSender(prop);
	}
	
	@Test
	public void senderRemovedTest(){
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
		prop.put("ptype","spam");
		prop.put("payload","Default");
		prop.put("psize","32");
		prop.put("pps","10");
		prop.put("ttl","32");
		
		Sender obj = c.addSender(prop);
		
		Collection<Sender> coll =  new ArrayList<Sender>();
		coll.add(obj);
		
		c.removeStreams(coll);
	}
	
	@Test
	public void newErrorEventTest(){
		cli = new CommandLineView();
		assertTrue(cli != null);

		c = Controller.getController();
		String initString[] = {"-nogui"};
		c.init(initString);
		assertTrue(c != null);

		cli.init(c);
		
		ErrorEvent err = new ErrorEvent();
		err.setErrorLevel(ErrorEventManager.ERROR);
		err.setAdditionalErrorMessage("Test Error");
		
		c.reportErrorEvent(err);
		
		err.setErrorLevel(ErrorEventManager.WARNING);
		err.setAdditionalErrorMessage("Test Warning");
		
		c.reportErrorEvent(err);
	}
	
	@Test
	public void killTest(){
		cli = new CommandLineView();
		assertTrue(cli != null);

		c = Controller.getController();
		String initString[] = {"-nogui"};
		c.init(initString);
		assertTrue(c != null);

		cli.init(c);
		
		cli.kill();
	}
}
