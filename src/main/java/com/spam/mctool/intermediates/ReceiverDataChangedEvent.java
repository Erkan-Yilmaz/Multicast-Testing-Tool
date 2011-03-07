package com.spam.mctool.intermediates;

import com.spam.mctool.model.Receiver;

public class ReceiverDataChangedEvent {

	Receiver receiver;
	
	public ReceiverDataChangedEvent(Receiver r) {
		this.receiver = r;
	}
	
	public Receiver getReceiver() {
		return this.receiver;
	}
	
}
