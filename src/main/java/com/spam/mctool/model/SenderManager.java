package com.spam.mctool.model;

/**
 * Interface provided by the sender pool to manage sending multicast streams.
 * @author Jeffrey Jedele
 */
public interface SenderManager {

	//TODO change parameter type to map in uml, change return type to sender
	/**
	 * Create a new sender.
	 * Param Description:
	 * group - ip address for multicast group to send to
	 * port - port for mulicast socket
	 * pps - packets per second to send
	 * psize - size of sent packets
	 * ttl - time to live of sent packets
	 * payload - text to send along
	 * ptype - type of packet format ("spam" | "hmann")
	 * @param params see description above for possible values
	 * @return the created sender
	 */
	public Sender create(java.util.Map<String, String> params);
	
	/**
	 * Remove a sender.
	 * @param sender sender to be removed
	 */
	public void remove(com.spam.mctool.model.Sender sender);
	
	//TODO change return to type to collection in uml
	/**
	 * @return collection of all senders in the pool
	 */
	public java.util.Collection<com.spam.mctool.model.Sender> getSenders();
	
	/**
	 * stops all senders in the pool
	 */
	public void killAll();
	
	/**
	 * SenderAddedOrRemoved Observer
	 * @param l listener to be added
	 */
	public void addSenderAddedOrRemovedListener(com.spam.mctool.model.SenderAddedOrRemovedListener l);
	
	/**
	 * SenderAddedOrRemoved Observer
	 * @param l listener to be removed
	 */
	public void removeSenderAddedOrRemovedListener(com.spam.mctool.model.SenderAddedOrRemovedListener l);
	
}
