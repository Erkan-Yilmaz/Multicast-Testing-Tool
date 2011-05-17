package com.spam.mctool.controller;

/**
 * @author David Hildenbrand
 * This interface is to be implemented by every class that has to be notified
 * when the profile of the controller changes.
 */
public interface ProfileChangeListener {

	/**
	 * This method is called when the profile of the controller changes.
	 * @param e change event which contains the profile
	 */
	void profileChanged(com.spam.mctool.intermediates.ProfileChangeEvent e);

}
