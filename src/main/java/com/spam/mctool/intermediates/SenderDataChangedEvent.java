package com.spam.mctool.intermediates;

import com.spam.mctool.model.Sender;

public class SenderDataChangedEvent {
	
	private Sender sender;
	
	public SenderDataChangedEvent(Sender s) {
		this.sender = s;
	}
	
	public Sender getSender() {
		return this.sender;
	}
	
}
