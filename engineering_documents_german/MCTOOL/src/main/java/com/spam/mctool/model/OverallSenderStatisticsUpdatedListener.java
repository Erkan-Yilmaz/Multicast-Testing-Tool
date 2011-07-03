package com.spam.mctool.model;

import com.spam.mctool.intermediates.OverallSenderStatisticsUpdatedEvent;

/**
 * This has to be implemented by all components that want to be notified
 * when new overall sender statistics are available (accumulation of all senders
 * in the system).
 * @author Jeffrey Jedele
 */
public interface OverallSenderStatisticsUpdatedListener {
	
	/**
	 * Called when new overall sender statistics are available.
	 * @param e event which holds the thrower
	 */
	public void overallSenderStatisticsUpdated(OverallSenderStatisticsUpdatedEvent e);

}
