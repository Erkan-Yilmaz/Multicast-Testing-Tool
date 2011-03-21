package com.spam.mctool.model.persistence;

import java.util.HashMap;
import java.util.LinkedList;

import com.spam.mctool.model.Sender;
import com.spam.mctool.model.SenderPool;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class SenderPoolXmlConverter implements Converter {

	public void marshal(Object obj, HierarchicalStreamWriter w,
			MarshallingContext context) {
		SenderPool sp = (SenderPool) obj;
		for(Sender s : sp.getSenders()) {
			w.startNode("sender");
			w.startNode("group");
			w.setValue(s.getGroup().getHostAddress());
			w.endNode();
			w.startNode("port");
			w.setValue(""+s.getPort());
			w.endNode();
			w.startNode("networkInterface");
			w.setValue(s.getNetworkInterface().getDisplayName());
			w.endNode();
			w.startNode("packetsPerSecond");
			w.setValue(""+s.getSenderConfiguredPacketRate());
			w.endNode();
			w.startNode("packetSize");
			w.setValue(""+s.getPacketSize());
			w.endNode();
			w.startNode("packetType");
			w.setValue(""+s.getpType());
			w.endNode();
			w.startNode("ttl");
			w.setValue(""+s.getTtl());
			w.endNode();
			w.startNode("payload");
			w.setValue(s.getPayloadAsString());
			w.endNode();
			w.startNode("analyzingLevel");
			w.setValue(""+s.getAnalyzingBehaviour());
			w.endNode();
			w.endNode();
		}
	}

	public Object unmarshal(HierarchicalStreamReader r,
			UnmarshallingContext context) {
		LinkedList<HashMap<String, String>> values = new LinkedList<HashMap<String, String>>();
		while(r.hasMoreChildren()) {
			r.moveDown();
			while(r.hasMoreChildren()) {
				r.moveDown();
				System.out.println(r.getNodeName()+": "+r.getValue());
				r.moveUp();
			}
			r.moveUp();
		}
		return null;
	}

	public boolean canConvert(Class arg0) {
		return arg0.equals(SenderPool.class);
	}

}
