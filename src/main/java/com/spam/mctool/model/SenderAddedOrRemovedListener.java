package com.spam.mctool.model;

/**
 * This interface is implemented by all classes that have to be notified when a sender
 * was added or removed.
 * @author Jeffrey Jedele
 */
public interface SenderAddedOrRemovedListener {
	
	/**
	 * @param e event containing the added sender
	 */
	public void senderAdded(com.spam.mctool.intermediates.SenderAddedOrRemovedEvent e);
	
	/**
	 * @param e event containing the removed sender
	 */
	public void senderRemoved(com.spam.mctool.intermediates.SenderAddedOrRemovedEvent e);
	
}
