package com.spam.mctool.controller;

/**
 * This interface is to be implemented by every class that has to be notified
 * when the profile of the controller changes.
 * @author Jeffrey Jedele
 */
public interface ProfileChangeListener {
	
	/**
	 * This method is called when the profile of the controller changes.
	 * @param e change event which contains the profile
	 */
	public void profileChanged(com.spam.mctool.intermediates.ProfileChangeEvent e);
	
}
