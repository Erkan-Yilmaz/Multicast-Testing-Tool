package com.spam.mctool.controller;

public interface ErrorEventManager {

    /**
     * This method registers a new listener to the ErrorEventManager.
     * @param l The listener to be added. Must not be null.
     * @param errorLevel If <0 it is set to 0, if >5 it is set to 5.
     * @throws IllegalArgumentException() if the listener l is null.
     */
    public void addErrorEventListener(com.spam.mctool.controller.ErrorEventListener l, int errorLevel);

    /**
     * This method removes a listener from the ErrorEventManager observers list.
     * @param l The listener to be removed. Must not be null
     * @throws IllegalArgumentException() if the listener l is null.
     */
    public void removeErrorEventListener(com.spam.mctool.controller.ErrorEventListener l);

    /**
     * This method is used to report an ErrorEvent to the ErrorEventManager
     * and to all the observers. If no observer is registered the ErrorEventManager will
     * print the ErrorEvents to stdout.
     * @param e The ErrorEvent to be reported. Must not be null.
     * @throws IllegalArgumentException() if the ErrorEvent e is null.
     */
    public void reportErrorEvent(ErrorEvent e);

}
