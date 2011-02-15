package com.spam.mctool.controller;

/**
 * This interface is implemented by the controller and provides the functionality to work
 * with the configuration profiles.
 * @author Jeffrey Jedele
 */
public interface ProfileManager {
	
	/**
	 * Save the current profile to a file.
	 */
	public void saveProfile();
	
	//TODO how does controller know which profile to load?
	/**
	 * Load a new profile in the controller.
	 */
	public void loadProfile();
	
	/**
	 * Add a ProfileChangeListener to the observer list.
	 * @param listener listener to be added.
	 */
	public void addProfileChangeListener(com.spam.mctool.controller.ProfileChangeListener listener);
	
	/**
	 * Remove a ProfileChangeListener to the observer list.
	 * @param listener listener to be removed.
	 */
	public void removeProfileChangeListener(com.spam.mctool.controller.ProfileChangeListener listener);
	
	/**
	 * @param l language to be set as default.
	 */
	public void setDefaultLanguage(Object l);
	
	/**
	 * @return default language of the controller.
	 */
	public Object getDefaultLanguage();
	
}
