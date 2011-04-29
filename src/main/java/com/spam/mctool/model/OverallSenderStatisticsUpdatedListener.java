package com.spam.mctool.model;

import com.spam.mctool.intermediates.OverallSenderStatisticsUpdatedEvent;

public interface OverallSenderStatisticsUpdatedListener {
	
	public void overallSenderStatisticsUpdated(OverallSenderStatisticsUpdatedEvent e);

}
