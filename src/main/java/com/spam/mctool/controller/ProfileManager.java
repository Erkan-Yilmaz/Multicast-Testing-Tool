package com.spam.mctool.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    //public void storeCurrentProfile();

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
         * @param path The path to the file.
         */
        void loadProfile(File path);

        /**
         * Save profile data
         * @param p The profile containing the name and path for the profile
         */
        void saveProfile(Profile p);
        
        /**
         * Save the profile to the settings defined in currentProfile
         */
        void saveCurrentProfile();

        /**
         * Change the profile to currentProfile
         * @param currentProfile the new profile
         */
        void setCurrentProfile(Profile currentProfile);
}
