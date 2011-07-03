package com.spam.mctool.model;

import static org.junit.Assert.*;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

/**
 * TC: M02
 * Ensures correct function of getters and setters in MulticastStream.
 * @author Jeffrey Jedele
 */
public class MulticastStreamTest {
	
	private Object[] ipmcvalid;
	private Object[] ipmcinvalid;
	private Object[] validports;
	private Object[] invalidports;
	private MulticastStream ms;
	
	@Before
	public void setup() throws UnknownHostException {
		ms = new MulticastStream() {
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
		 
		ipmcvalid = new Object[]{
			"224.0.0.1",
			"239.255.255.254",
			"ff00:::::::1",
			"ffff:ffff:ffff:ffff:ffff:ffff:ffff:fffe",
			InetAddress.getByName("224.0.0.1"),
			InetAddress.getByName("239.255.255.254")
		};
		
		ipmcinvalid = new Object[] {
			"223.255.255.254",
			"224.0.0.0",
			"239.255.255.255",
			"240.0.0.1",
			"feff:::::::1",
			"ff00:::::::0",
			"ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff",
			":::::::",
			"0.0.0",
			"totallywrong"
		};
		
		validports = new Object[] {
			"0",
			"1025",
			"65535"
		};
		
		invalidports = new Object[] {
			"-1",
			"totallywrong"
		};
	}
	
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
	}
	
	 @Test
	 public void testIfPacketTypeEnumerationWorks() {
		 MulticastStream.PacketType ptype = MulticastStream.PacketType.getByIdentifier("spam");
		 assertEquals("returned packet format was not correct", MulticastStream.PacketType.SPAM, ptype);
		 assertEquals("spam packet format did not return correct display name", "Spam Packet Format", ptype.getDisplayName());
		 
		 ptype = MulticastStream.PacketType.getByIdentifier("hmann");
		 assertEquals("returned packet format was not correct", MulticastStream.PacketType.HMANN, ptype);
		 assertEquals("hmann packet format did not return correct display name", "Hirschmann Packet Format", ptype.getDisplayName());
		 
	 }
	 
	 @Test
	 public void testIfGettersAndSettersWorkCorrectly() throws UnknownHostException, SocketException {
		 
		 // valid ip group values
		 for(Object o : ipmcvalid) {
			 assertTrue("group not accepted: "+o.toString(), ms.setGroup(o));
		 }
		 
		 // invalid ip group values
		 for(Object o : ipmcinvalid) {
			 assertFalse("false group accepted: "+o.toString(), ms.setGroup(o));
		 }
		 
		 // valid ports
		 for(Object o : validports) {
			 assertTrue("port not accepted: "+o.toString(), ms.setPort(o));
		 }
		 
		// invalid ports
		 for(Object o : invalidports) {
			 assertFalse("port accepted: "+o.toString(), ms.setPort(o));
		 }
		 
		 // no test of 
		 
		 // is ip mode remembered correctly
		 InetAddress group = InetAddress.getByName("224.0.0.1");
		 ms.setGroup(group);
		 assertEquals("inet address not correctly returned", group, ms.getGroup());
		 assertEquals("ip mode not correctly set", Inet4Address.class, ms.ipMode);
		 
		 group = InetAddress.getByName("FF02:0:0:0:0:0:0:1");
		 ms.setGroup(group);
		 assertEquals("inet address not correctly returned", group, ms.getGroup());
		 assertEquals("ip mode not correctly set", Inet6Address.class, ms.ipMode);
		 
		 // network interface setter
		 NetworkInterface ninf = NetworkInterface.getByInetAddress(InetAddress.getByName("127.0.0.1"));
		 ms.setNetworkInterface(ninf);
		 assertEquals("network interface not correctly returned", ninf, ms.getNetworkInterface());
		 
		 // is active 
		 MulticastStream.State state = MulticastStream.State.ACTIVE;
		 ms.state = state;
		 assertEquals("activity state not returned correctly", state, ms.getState());
		 assertTrue("activity state not returned correctly", ms.isActive());
		 state = MulticastStream.State.INACTIVE;
		 ms.state = state;
		 assertEquals("activity state not returned correctly", state, ms.getState());
		 assertFalse("activity state not returned correctly", ms.isActive());
		 
		 // analyzing behaviour
		 MulticastStream.AnalyzingBehaviour abeh = MulticastStream.AnalyzingBehaviour.DEFAULT;
		 ms.setAnalyzingBehaviour(abeh);
		 assertEquals("analyzing behaviour not returned correctly", abeh, ms.getAnalyzingBehaviour());
		 
		 // status calculation interval
		 int si = 1000;
		 ms.setStatsInterval(si);
		 assertEquals("stats interval not returned correctly", si, ms.getStatsInterval());
	 }

}
