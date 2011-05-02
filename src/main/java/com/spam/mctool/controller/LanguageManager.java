package com.spam.mctool.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * This interface is implemented by the controller and provides the functionality to
 * change the global language and inform all listeners
 * @author David Hildenbrand
 */
public interface LanguageManager {

    /**
     * Add an listener for the languageChange events.
     * @param l listener to be added
     */
    public void addLanguageChangeListener(com.spam.mctool.controller.LanguageChangeListener l);

    /**
     * Remove a listener from the list of listeners for the languageChange event.
     * @param l listener to be removed
     */
    public void removeLanguageChangeListener(com.spam.mctool.controller.LanguageChangeListener l);

    /**
     * Inform all observers about a language change
     */
    public void reportLanguageChange();
}
