package com.spam.mctool.model;

/**
 * This interface is implemented by all classes that have to be notified when a receiver
 * was added or removed.
 * @author Jeffrey Jedele
 */
public interface ReceiverAddedOrRemovedListener {
	
	/**
         * Notifies observers that a new receiver group was added to the model
         *
	 * @param e event containing the added receiver
	 */
	public void receiverGroupAdded(com.spam.mctool.intermediates.ReceiverAddedOrRemovedEvent e);
	
	/**
         * Notifies observers that a receiver group was removed from the model
         *
	 * @param e event containing the removed receiver
	 */
	public void receiverGroupRemoved(com.spam.mctool.intermediates.ReceiverAddedOrRemovedEvent e);
	
}
