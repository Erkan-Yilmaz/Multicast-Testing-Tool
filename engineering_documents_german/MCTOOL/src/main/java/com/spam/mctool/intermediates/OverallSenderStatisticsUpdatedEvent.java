package com.spam.mctool.intermediates;

import com.spam.mctool.model.SenderManager;

public class OverallSenderStatisticsUpdatedEvent {
	
	private SenderManager source;
	
	public OverallSenderStatisticsUpdatedEvent(SenderManager source) {
		this.source = source;
	}
	
	public SenderManager getSource() {
		return source;
	}

}
