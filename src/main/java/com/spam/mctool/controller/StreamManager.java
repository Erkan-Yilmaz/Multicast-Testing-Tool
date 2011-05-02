package com.spam.mctool.controller;

import com.spam.mctool.view.GraphicalView;

;

/**
 * @author Jeffrey Jedele
 * This interface is used provided by the controller and used
 * by the view to work with multicast streams in the controller
 * layer.
 */
/**
 * @author davidhildenbrand
 *
 */
public interface StreamManager {

	//TODO change uml to map paramaters
	/**
	 * Add a new sender to the model layer.
	 * @param params Properties for the new created sender. Look in moddocs for possible values
	 * @return the newly created sender.
	 */
	public com.spam.mctool.model.Sender addSender(java.util.Map<String, String> params);

	//TODO change uml to map paramaters
	/**
	 * Add a new receiver to the model layer.
	 * @param params Properties for the new created receiver. Look in moddocs for possible values
	 * @return the newly created receiver.
	 */
	public com.spam.mctool.model.ReceiverGroup addReceiverGroup(java.util.Map<String, String> params);

	//TODO are individual remove methods for senders and receivers really necessary
	/**
	 * Remove a set of streams from the model layer.
	 * @param streams streams to be removed from the model layer.
	 */
	public void removeStreams(java.util.Collection <? extends com.spam.mctool.model.MulticastStream> streams);

	/**
	 * Start a collection of streams.
	 * @param streams streams to be started.
	 */
	public void startStreams(java.util.Collection<? extends com.spam.mctool.model.MulticastStream> streams);

	/**
	 * Stop a collection of streams.
	 * @param streams streams to be stopped.
	 */
	public void stopStreams(java.util.Collection<? extends com.spam.mctool.model.MulticastStream> streams);

	/**
	 * @return collection of all senders in the pool
	 */
	public java.util.Collection<com.spam.mctool.model.Sender> getSenders();

	/**
	/**
	 * @return collection of all receivers in the pool
	 */
	public java.util.Collection<com.spam.mctool.model.ReceiverGroup> getReceiverGroups();

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

	/**
	 * OverallReceiverStatisticsUpdated Observer
     * @param l listener to be added
     */
    public void addOverallReceiverStatisticsUpdatedListener(com.spam.mctool.model.OverallReceiverStatisticsUpdatedListener l);

    /**
     * OverallSenderStatisticsUpdated Observer
     * @param l listener to be added
     */
    public void addOverallSenderStatisticsUpdatedListener(com.spam.mctool.model.OverallSenderStatisticsUpdatedListener l);

    /**
     * OverallReceiverStatisticsUpdated Observer
     * @param l listener to be removed
     */
    public void removeOverallReceiverStatisticsUpdatedListener(com.spam.mctool.model.OverallReceiverStatisticsUpdatedListener l);

    /**
     * OverallSenderStatisticsUpdated Observer
     * @param l listener to be removed
     */
    public void removeOverallSenderStatisticsUpdatedListener(com.spam.mctool.model.OverallSenderStatisticsUpdatedListener l);

}
