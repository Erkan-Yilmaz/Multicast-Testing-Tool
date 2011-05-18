package com.spam.mctool.controller;

/**
 * This interface must be implemented by all classes wanting to receiver updates about the language after suscribing to the LanguageManager.
 * @author David Hildenbrand
 */
public interface LanguageChangeListener {

    /**
     * After subscribing to the LanguageChangeManager this method is called
     * by the LanguageChangeManager to inform about a changed language.
     */
    public void languageChanged();

}
