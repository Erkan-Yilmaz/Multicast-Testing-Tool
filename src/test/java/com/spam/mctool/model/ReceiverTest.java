package com.spam.mctool.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import com.spam.mctool.model.packet.AutoPacket;

/**
 * TC: M05
 * Ensures that statistics of receiver are calculated correctly
 * @author Jeffrey Jedele
 */
public class ReceiverTest {
	private Receiver r;
	
	@Before
	public void tearUp() {
		r = new Receiver(11111111, MulticastStream.AnalyzingBehaviour.EAGER);
		r.setTimeout(1000);
	}

	@Test
	public void testAliveAndAliveStateChangedMechanism() {
		// 1st analyze - new receiver, no packets
		r.proposeStatisticsRenewal();
		assertEquals("alive with no packets", false, r.isAlive());
		assertEquals("alive state change with no packets", false, r.hasChangedAliveState());
		
		// 2nd analyze - first packet received
		ReceiverGroup.PacketContainer pc1 = new ReceiverGroup.PacketContainer();
		pc1.packet = new AutoPacket();
		pc1.systemTime = System.nanoTime();
		r.addPacketContainer(pc1);
		r.proposeStatisticsRenewal();
		assertEquals("not alive but new packet", true, r.isAlive());
		assertEquals("no state change but got to live", true, r.hasChangedAliveState());
		
		// 3rd analyze - usual operation
		ReceiverGroup.PacketContainer pc2 = new ReceiverGroup.PacketContainer();
		pc2.packet = new AutoPacket();
		pc2.systemTime = System.nanoTime();
		r.addPacketContainer(pc2);
		r.proposeStatisticsRenewal();
		assertEquals("not alive but packets arrive", true, r.isAlive());
		assertEquals("state change but only remained active", false, r.hasChangedAliveState());

		// 4th analyze - simulated timeout
		ReceiverGroup.PacketContainer pc3 = new ReceiverGroup.PacketContainer();
		pc3.packet = new AutoPacket();
		pc3.systemTime = System.currentTimeMillis()-5000;
		r.addPacketContainer(pc3);
		r.proposeStatisticsRenewal();
		assertEquals("alive but paket timeout should be triggered", false, r.isAlive());
		assertEquals("no state change but should have left alive", true, r.hasChangedAliveState());
		
		// 5th analyzing cycle - continued timeout
		r.proposeStatisticsRenewal();
		assertEquals("entered active state but no new packet", false, r.isAlive());
		assertEquals("state change indicated but should only remain dead", false, r.hasChangedAliveState());
		
		// 6th analyze - packet after timeout receiver
		ReceiverGroup.PacketContainer pc4 = new ReceiverGroup.PacketContainer();
		pc4.packet = new AutoPacket();
		pc4.systemTime = System.nanoTime();
		r.addPacketContainer(pc4);
		r.proposeStatisticsRenewal();
		assertEquals("not alive but timeout should be over", true, r.isAlive());
		assertEquals("no state change but shold be active again", true, r.hasChangedAliveState());

		// 7th analyze -continued usual operation
		ReceiverGroup.PacketContainer pc5 = new ReceiverGroup.PacketContainer();
		pc5.packet = new AutoPacket();
		pc5.systemTime = System.nanoTime();
		r.addPacketContainer(pc5);
		r.proposeStatisticsRenewal();
		assertEquals("not alive but packets arrive", true, r.isAlive());
		assertEquals("state change but only remained active", false, r.hasChangedAliveState());
	}
	
	@Test
	public void testIfStatisticsAreCalculatedCorrectly() {
		// fill packet queue for avg calc
		for(int i=1; i<=10; i++) {
			ReceiverGroup.PacketContainer pc = new ReceiverGroup.PacketContainer();
			pc.packet = new AutoPacket();
			pc.packet.setDispatchTime(i-1);
			pc.systemTime = i;
			pc.receivedTime = i*(long) 10e6; // simulate pps of 100
			r.addPacketContainer(pc);
		}
		
		r.proposeStatisticsRenewal();
		assertEquals("min. pps not correct", 100, r.getMinPPS());
		assertEquals("max. pps not correct", 100, r.getMaxPPS());
		assertEquals("min. traversal not correct", 1, r.getMinTraversal());
		assertEquals("avg. traversal not correct", 1, r.getAvgTraversal());
		assertEquals("max. traversal not correct", 1, r.getMaxTraversal());
		assertEquals("max. delay not correct", 10, r.getMaxDelay());
		assertEquals("received packets not correct", 10, r.getReceivedPackets());
	}

}
