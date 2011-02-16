package com.spam.mctool.model;

/**
 * This interface is implemented by all classes that have to be notified when a receiver
 * was added or removed.
 * @author Jeffrey Jedele
 */
public interface ReceiverAddedOrRemovedListener {
	
	/**
	 * @param e event containing the added receiver
	 */
	public void receiverAdded(com.spam.mctool.intermediates.ReceiverAddedOrRemovedEvent e);
	
	/**
	 * @param e event containing the removed receiver
	 */
	public void receiverRemoved(com.spam.mctool.intermediates.ReceiverAddedOrRemovedEvent e);
	
}
