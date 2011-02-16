package com.spam.mctool.view;

/**
 * This interface has to be implemented by every view component.
 * @author Jeffrey Jedele
 */
public interface MctoolView {

	//TODO replace coneceptual AbstractView in uml with this
	/**
	 * Run after the view is constructed
	 * @param c the application controller to be stored.
	 */
	public void init(com.spam.mctool.controller.Controller c);
	
}
