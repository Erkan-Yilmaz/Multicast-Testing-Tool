package com.spam.mctool.controller;

public interface ErrorEventListener {

    /**
     * After subscribing to the ErrorEventManager this method is called
     * by the ErrorEventManager to inform about a new error event.
     * ErrorEvents with an errorLevel of 0 should only be reported as debug messages.
     * @param e The error event that has bee reported to the manager.
     */
    public void newErrorEvent(ErrorEvent e);

}
