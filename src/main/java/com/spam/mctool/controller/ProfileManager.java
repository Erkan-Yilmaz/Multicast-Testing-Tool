package com.spam.mctool.controller;

import java.io.File;
import java.util.List;

/**
 * This interface is implemented by the controller and provides the functionality to work
 * with the configuration profiles.
 * @author Jeffrey Jedele
 */
public interface ProfileManager {

    /**
     * Add an listener for the profileChange events.
     * @param l listener to be added
     */
    void addProfileChangeListener(com.spam.mctool.controller.ProfileChangeListener l);

    /**
     * Remove a listener from the list of listeners for the profileChange event.
     * @param l listener to be removed
     */
    void removeProfileChangeListener(com.spam.mctool.controller.ProfileChangeListener l);

    /**
     * Get the current profile.
     * @return current profile. Can be null if no profile has been set yet.
     */
    Profile getCurrentProfile();

    /**
     * Get list of recently used profiles
     * @return list of recently used profiles. Never null.
     */
    List<Profile> getRecentProfiles();

    /**
     * Load profile data from the path given. Will remove all senders and receivers and load the state stored
     * in the given file. The name of the profile will also be red.
     * If successful, a profileChanged event will be reported.
     * @param path The path to the file.
     */
    void loadProfile(File path);

    /**
     * Save the current state of senders and receivers to a profile file.
     * @param p The profile containing the name and path for the profile. Must not be null!
     */
    void saveProfile(Profile p);

    /**
     * Save the profile to the path defined in currentProfile.
     * If currentProfile is null(no profile has been set yet) an error is reported.
     */
    void saveCurrentProfile();

    /**
     * Set the profile to currentProfile. Will not load any data, only change the information for the profile.
     * After setting the profile, a profileChanged event is fired.
     * @param currentProfile the new profile
     */
    void setCurrentProfile(Profile currentProfile);
}
