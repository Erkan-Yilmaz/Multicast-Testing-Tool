package com.spam.mctool.intermediates;

import com.spam.mctool.model.ReceiverPool;

public class OverallReceiverStatisticsUpdatedEvent {
	
	private ReceiverPool source;
	
	public OverallReceiverStatisticsUpdatedEvent(ReceiverPool source) {
		this.source = source;
	}
	
	public ReceiverPool getSource() {
		return source;
	}
	
}
