package com.spam.mctool.controller;

/**
 * @author David Hildenbrand
 * This interface is implemented by the controller and provides the functionality to
 * change the global language and inform all listeners
 */
public interface LanguageManager {

    /**
     * Add an listener for the languageChange events.
     * @param l listener to be added
     */
    void addLanguageChangeListener(com.spam.mctool.controller.LanguageChangeListener l);

    /**
     * Remove a listener from the list of listeners for the languageChange event.
     * @param l listener to be removed
     */
    void removeLanguageChangeListener(com.spam.mctool.controller.LanguageChangeListener l);

    /**
     * Inform all observers about a language change
     */
    void reportLanguageChange();
}
