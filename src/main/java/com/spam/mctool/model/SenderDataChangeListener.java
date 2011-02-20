package com.spam.mctool.model;

/**
 * This interface is implemented by all classes that have to be informed when a sender has gathered
 * new data.
 * @author Jeffrey Jedele
 */
public interface SenderDataChangeListener {
	
	/**
	 * Executed when sender gathered new data
	 * @param e Event that contains the sender
	 */
	public void dataChanged(com.spam.mctool.intermediates.SenderDataChangedEvent e);
	
}
