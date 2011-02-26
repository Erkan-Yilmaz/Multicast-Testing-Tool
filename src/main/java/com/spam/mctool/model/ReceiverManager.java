package com.spam.mctool.model;

/**
 * Interface provided by the receiver pool to manage sending multicast streams.
 * @author Jeffrey Jedele
 */
public interface ReceiverManager {

	//TODO change parameter type to map in uml, change return type to receiver
	/**
	 * Create a new receiver.
	 * @param params See moddoc for possible values
	 * @return the created receiver
	 */
	public com.spam.mctool.model.Receiver create(java.util.Map<String, String> params);
	
	/**
	 * Remove a receiver.
	 * @param receiver receiver to be removed
	 */
	public void remove(com.spam.mctool.model.Receiver receiver);
	
	//TODO change return to type to collection in uml
	/**
	 * @return collection of all receivers in the pool
	 */
	public java.util.Collection<com.spam.mctool.model.Receiver> getReceiver();
	
	/**
	 * stops all receivers in the pool
	 */
	public void killAll();
	
	/**
	 * ReceiverAddedOrRemoved Observer
	 * @param l listener to be added
	 */
	public void addReceiverAddedOrRemovedListener(com.spam.mctool.model.ReceiverAddedOrRemovedListener l);
	
	/**
	 * ReceiverAddedOrRemoved Observer
	 * @param l listener to be removed
	 */
	public void removeReceiverAddedOrRemovedListener(com.spam.mctool.model.ReceiverAddedOrRemovedListener l);
	
}
