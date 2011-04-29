package com.spam.mctool.model;

import com.spam.mctool.intermediates.OverallReceiverStatisticsUpdatedEvent;

public interface OverallReceiverStatisticsUpdatedListener {

	void overallReceiverStatisticsUpdated(OverallReceiverStatisticsUpdatedEvent e);
	
}
