package com.spam.mctool.controller;

import java.util.List;

/**
 * This interface is implemented by the controller and provides the functionality to work
 * with the configuration profiles.
 * @author Jeffrey Jedele
 */
public interface ProfileManager {
	
	/**
	 * Saves the current settings and streams to a file
	 */
	public void storeCurrentProfile();
	
	/**
	 * ProfileChangeListener Observer
	 * @param l listener to be added 
	 */
	public void addProfileChangeListener(com.spam.mctool.controller.ProfileChangeListener l);
	
	/**
	 * ProfileChangeListener Observer
	 * @param l listener to be removed
	 */
	public void removeProfileChangeListener(com.spam.mctool.controller.ProfileChangeListener l);

        /**
         * Get the current profile.
         * @return current profile
         */
        Profile getCurrentProfile();

        /**
         * Get list of recently used profiles
         * @return list of recently used profiles
         */
        List<Profile> getRecentProfiles();

        /**
         * Load profile data
         */
        void loadProfile();

        /**
         * Save profile data
         */
        void saveProfile();

        /**
         * Change the profile to currentProfile
         * @param currentProfile the new profile
         */
        void setCurrentProfile(Profile currentProfile);


}
