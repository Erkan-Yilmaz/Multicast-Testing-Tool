package com.spam.mctool.model;

import static org.junit.Assert.*;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.junit.Test;


public class MulticastStreamTest {
	
	@Test
	public void testIfAnalyzingBehaviourEnumerationWorks() {
		MulticastStream.AnalyzingBehaviour abeh = MulticastStream.AnalyzingBehaviour.getByIdentifier("lazy");
		assertEquals("minimum stepwidth for lazy not ok", 1, abeh.getDynamicStatsStepWidth(1));
		assertEquals("maximum stepwidth for lazy not ok", 101, abeh.getDynamicStatsStepWidth(1000));
		assertEquals("divisor for lazy not ok", 4, abeh.getDiv());
		assertEquals("identifier for lazy not ok", "lazy", abeh.getIdentifier());
		
		abeh = MulticastStream.AnalyzingBehaviour.getByIdentifier("default");
		assertEquals("minimum stepwidth for default not ok", 1, abeh.getDynamicStatsStepWidth(1));
		assertEquals("maximum stepwidth for default not ok", 50, abeh.getDynamicStatsStepWidth(1000));
		assertEquals("divisor for default not ok", 2, abeh.getDiv());
		assertEquals("identifier for default not ok", "default", abeh.getIdentifier());
		
		abeh = MulticastStream.AnalyzingBehaviour.getByIdentifier("eager");
		assertEquals("minimum stepwidth for eager not ok", 1, abeh.getDynamicStatsStepWidth(1));
		assertEquals("maximum stepwidth for eager not ok", 20, abeh.getDynamicStatsStepWidth(1000));
		assertEquals("divisor for eager not ok", 1, abeh.getDiv());
		assertEquals("identifier for eager not ok", "eager", abeh.getIdentifier());
		
		exceptest:{
			try{
				MulticastStream.AnalyzingBehaviour.getByIdentifier("not existent");
			} catch(IllegalArgumentException e) {
				break exceptest;
			}
			// should not be reached
			fail("exception not thrown although illegal argument was given as identifier for analyzing behaviour");
		}
	}
	
	 @Test
	 public void testIfPacketTypeEnumerationWorks() {
		 MulticastStream.PacketType ptype = MulticastStream.PacketType.getByIdentifier("spam");
		 assertEquals("returned packet format was not correct", MulticastStream.PacketType.SPAM, ptype);
		 assertEquals("spam packet format did not return correct display name", "Spam Packet Format", ptype.getDisplayName());
		 
		 ptype = MulticastStream.PacketType.getByIdentifier("hmann");
		 assertEquals("returned packet format was not correct", MulticastStream.PacketType.HMANN, ptype);
		 assertEquals("hmann packet format did not return correct display name", "Hirschmann Packet Format", ptype.getDisplayName());
		 
		 exceptiontest:{
			 try {
				 MulticastStream.PacketType.getByIdentifier("notexistent");
			 } catch(IllegalArgumentException e) {
				 break exceptiontest;
			 }
			 // should not be reached
			 fail("exception not thrown although illegal was given as identifier for analyzing behaviour");
		 }
	 }
	 
	 @Test
	 public void testIfGettersAndSettersWorkCorrectly() throws UnknownHostException, SocketException {
		 MulticastStream ms = new MulticastStream() {
			@Override
			public void run() {
			}
			@Override
			public void activate() {
			}
			@Override
			public void deactivate() {
			}
		 };
		 
		 InetAddress group = InetAddress.getByName("224.0.0.1");
		 ms.setGroup(group);
		 assertEquals("inet address not correctly returned", group, ms.getGroup());
		 assertEquals("ip mode not correctly set", Inet4Address.class, ms.ipMode);
		 
		 group = InetAddress.getByName("FF02:0:0:0:0:0:0:1");
		 ms.setGroup(group);
		 assertEquals("inet address not correctly returned", group, ms.getGroup());
		 assertEquals("ip mode not correctly set", Inet6Address.class, ms.ipMode);
		 
		 int port = 1234;
		 ms.setPort(port);
		 assertEquals("port not correctly returned", port, ms.getPort());
		 
		 NetworkInterface ninf = NetworkInterface.getByInetAddress(InetAddress.getByName("127.0.0.1"));
		 ms.setNetworkInterface(ninf);
		 assertEquals("network interface not correctly returned", ninf, ms.getNetworkInterface());
		 
		 MulticastStream.State state = MulticastStream.State.ACTIVE;
		 ms.state = state;
		 assertEquals("activity state not returned correctly", state, ms.getState());
		 assertTrue("activity state not returned correctly", ms.isActive());
		 state = MulticastStream.State.INACTIVE;
		 ms.state = state;
		 assertEquals("activity state not returned correctly", state, ms.getState());
		 assertFalse("activity state not returned correctly", ms.isActive());
		 
		 MulticastStream.AnalyzingBehaviour abeh = MulticastStream.AnalyzingBehaviour.DEFAULT;
		 ms.setAnalyzingBehaviour(abeh);
		 assertEquals("analyzing behaviour not returned correctly", abeh, ms.getAnalyzingBehaviour());
		 
		 int si = 1000;
		 ms.setStatsInterval(si);
		 assertEquals("stats interval not returned correctly", si, ms.getStatsInterval());
	 }

}
