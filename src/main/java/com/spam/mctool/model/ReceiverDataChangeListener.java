package com.spam.mctool.model;

/**
 * This interface is implemented by all classes that have to be informed when a receiver has gathered
 * new data.
 * @author Jeffrey Jedele
 */
public interface ReceiverDataChangeListener {
	
	/**
	 * Executed when receiver gathered new data
	 * @param e Event that contains the receiver
	 */
	public void dataChanged(com.spam.mctool.intermediates.ReceiverDataChangedEvent e);
	
}
