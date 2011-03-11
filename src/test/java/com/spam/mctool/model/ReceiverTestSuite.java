package com.spam.mctool.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.spam.mctool.model.MulticastStream.AnalyzingBehaviour;
import com.spam.mctool.model.packet.Packet;
import com.spam.mctool.model.packet.SpamPacket;

public class ReceiverTestSuite {
	Receiver r;

	@Before
	public void setUp() throws Exception {
		r = new Receiver(1111111, MulticastStream.AnalyzingBehaviour.EAGER);
		for(int i=0; i<20; i++) {
			Packet p = new SpamPacket();
			p.setDispatchTime(i);
			p.setPayload(new String("test").getBytes());
			ReceiverGroup.PacketContainer pc = new ReceiverGroup.PacketContainer();
			pc.packet = p;
			pc.receivedTime = i+1;
			r.addPacketContainer(pc);
		}
	}

	@Test
	public void testCalcNewStatistics() {
		ReceiverStatistics stats = r.getStatistics();
		assertEquals(1000, stats.getReceiverMeasuredAvgPPS());
		assertEquals(1, stats.getAvgTraversal());
	}

}
