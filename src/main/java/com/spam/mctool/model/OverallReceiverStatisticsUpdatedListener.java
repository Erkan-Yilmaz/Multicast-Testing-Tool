package com.spam.mctool.model;

import com.spam.mctool.intermediates.OverallReceiverStatisticsUpdatedEvent;

/**
 * Implemented by all components that want to be informed about new
 * overall receiver statistics (accumulation of all receivers in the system).
 * @author Jeffrey Jedele
 */
public interface OverallReceiverStatisticsUpdatedListener {

	/**
	 * Called if new statistics are available.
	 * @param e event which holds the thrower.
	 */
	void overallReceiverStatisticsUpdated(OverallReceiverStatisticsUpdatedEvent e);

}
