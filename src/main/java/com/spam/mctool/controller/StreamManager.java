package com.spam.mctool.controller;;

/**
 * @author Jeffrey Jedele
 * This interface is used provided by the controller and used
 * by the view to work with multicast streams in the controller
 * layer.
 */
public interface StreamManager {

	/**
	 * Add a new sender to the model layer.
	 * @param params Properties for the new created sender. Look in moddocs for possible values
	 * @return the newly created sender.
	 */
	public com.spam.mctool.model.Sender addSender(java.util.HashMap<String, String> params);
	
	/**
	 * Add a new receiver to the model layer.
	 * @param params Properties for the new created receiver. Look in moddocs for possible values
	 * @return the newly created receiver.
	 */
	public com.spam.mctool.model.Receiver addReceiver(java.util.HashMap<String, String> params);
	
	/**
	 * Remove a set of streams from the model layer.
	 * @param streams streams to be removed from the model layer.
	 */
	public void removeStreams(java.util.Set<com.spam.mctool.model.MulticastStream> streams);
	
	/**
	 * Start a set of streams.
	 * @param streams streams to be started.
	 */
	public void startStreams(java.util.Set<com.spam.mctool.model.MulticastStream> streams);
	
	/**
	 * Stop a set of streams.
	 * @param streams streams to be stopped.
	 */
	public void stopStreams(java.util.Set<com.spam.mctool.model.MulticastStream> streams);
	
}
