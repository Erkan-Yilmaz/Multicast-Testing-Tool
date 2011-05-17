package com.spam.mctool.controller;

/**
 * @author David Hildenbrand
 * This interface has to be implemented by all classes which want to receiver Error Events later on.
 */
public interface ErrorEventListener {

    /**
     * After subscribing to the ErrorEventManager this method is called
     * by the ErrorEventManager to inform about a new error event.
     * ErrorEvents with an errorLevel of 0 should only be reported as debug messages.
     * @param e The error event that has bee reported to the manager.
     */
    void newErrorEvent(ErrorEvent e);

}
