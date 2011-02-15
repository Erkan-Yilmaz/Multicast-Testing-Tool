package com.spam.mctool.model;

/**
 * Interface provided by the sender pool to manage sending multicast streams.
 * @author Jeffrey Jedele
 */
public interface SenderManager {

	/**
	 * Create a new sender.
	 * @param params See moddoc for possible values
	 * @return the created sender
	 */
	public Sender create(java.util.Map<String, String> params);
	
	/**
	 * Remove a sender.
	 * @param sender sender to be removed
	 */
	public void remove(Sender sender);
	
	public void addSenderAddedOrRemovedListener();
	
}
