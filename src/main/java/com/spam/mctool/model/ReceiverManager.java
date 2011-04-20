package com.spam.mctool.model;

/**
 * Interface provided by the receiver pool to manage sending multicast streams.
 * @author Jeffrey Jedele
 */
public interface ReceiverManager {

	//TODO change parameter type to map in uml, change return type to receiver
	/**
	 * Create a new receiver. Possible values:
	 * @param params See moddoc for possible values
	 * group: ip address of multicast group to join
	 * port: port of multicast group
	 * ninf: ip address of network interface to receive packets
	 * abeh: analyzing granularity, may be EAGER, DEFAULT, LAZY, defaults to DEFAULT
	 * @return the created receiver
	 */
	public com.spam.mctool.model.ReceiverGroup create(java.util.Map<String, String> params);
	
	/**
	 * Remove a receiver.
	 * @param receiver receiver to be removed
	 */
	public void remove(com.spam.mctool.model.ReceiverGroup receiver);
	
	//TODO change return to type to collection in uml
	/**
	 * @return collection of all receivers in the pool
	 */
	public java.util.Collection<com.spam.mctool.model.ReceiverGroup> getReceiverGroups();
	
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
