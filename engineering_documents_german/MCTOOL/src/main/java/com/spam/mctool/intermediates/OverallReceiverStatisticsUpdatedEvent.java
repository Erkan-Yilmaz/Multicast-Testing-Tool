package com.spam.mctool.intermediates;

import com.spam.mctool.model.ReceiverManager;

public class OverallReceiverStatisticsUpdatedEvent {
	
	private ReceiverManager source;
	
	public OverallReceiverStatisticsUpdatedEvent(ReceiverManager source) {
		this.source = source;
	}
	
	public ReceiverManager getSource() {
		return source;
	}
	
}
