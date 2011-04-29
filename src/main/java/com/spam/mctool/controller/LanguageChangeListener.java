package com.spam.mctool.controller;

public interface LanguageChangeListener {

    /**
     * After subscribing to the LanguageChangeManager this method is called
     * by the LanguageChangeManager to inform about a changed language.
     */
    public void languageChanged();

}
