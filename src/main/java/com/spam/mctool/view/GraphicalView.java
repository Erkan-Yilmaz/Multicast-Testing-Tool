/**
 * 
 */
package com.spam.mctool.view;

import com.spam.mctool.controller.Controller;
import com.spam.mctool.controller.ProfileChangeListener;
import com.spam.mctool.intermediates.ProfileChangeEvent;
import com.spam.mctool.intermediates.ReceiverAddedOrRemovedEvent;
import com.spam.mctool.intermediates.ReceiverDataChangedEvent;
import com.spam.mctool.intermediates.SenderAddedOrRemovedEvent;
import com.spam.mctool.intermediates.SenderDataChangedEvent;
import com.spam.mctool.model.ReceiverAddedOrRemovedListener;
import com.spam.mctool.model.ReceiverDataChangeListener;
import com.spam.mctool.model.SenderAddedOrRemovedListener;
import com.spam.mctool.model.SenderDataChangeListener;

/**
 * @author Tobias Schoknecht, Tobias Stöckel
 * 
 * Represents the graphical user interface of the MCTool.
 *
 */
public class GraphicalView implements MctoolView,
		SenderDataChangeListener, ReceiverDataChangeListener,
		ProfileChangeListener, SenderAddedOrRemovedListener,
		ReceiverAddedOrRemovedListener {

	private MainFrame mainFrame;
	// TODO Add Dialogs
	//private Controller controller;
	//private Language language;

	public void receiverAdded(ReceiverAddedOrRemovedEvent e) {
		// TODO Auto-generated method stub

	}

	public void receiverRemoved(ReceiverAddedOrRemovedEvent e) {
		// TODO Auto-generated method stub

	}

	public void senderAdded(SenderAddedOrRemovedEvent e) {
		// TODO Auto-generated method stub

	}

	public void senderRemoved(SenderAddedOrRemovedEvent e) {
		// TODO Auto-generated method stub

	}

	public void profileChanged(ProfileChangeEvent e) {
		// TODO Auto-generated method stub

	}

	public void dataChanged(ReceiverDataChangedEvent e) {
		// TODO Auto-generated method stub

	}

	public void dataChanged(SenderDataChangedEvent e) {
		// TODO Auto-generated method stub

	}

	/**
	 * Initializes the graphical user interface and displays the main window.
	 */
	public void init(Controller c) {
		// TODO Auto-generated method stub
		mainFrame = new MainFrame();
		mainFrame.setVisible(true);
	}

}
