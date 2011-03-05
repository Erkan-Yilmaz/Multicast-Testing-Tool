/**
 * 
 */
package com.spam.mctool.view;

import com.spam.mctool.view.main.MainFrame;
import com.spam.mctool.controller.Controller;
import com.spam.mctool.controller.ProfileChangeListener;
import com.spam.mctool.controller.ProfileManager;
import com.spam.mctool.controller.StreamManager;
import com.spam.mctool.intermediates.ProfileChangeEvent;
import com.spam.mctool.intermediates.ReceiverAddedOrRemovedEvent;
import com.spam.mctool.intermediates.ReceiverDataChangedEvent;
import com.spam.mctool.intermediates.SenderAddedOrRemovedEvent;
import com.spam.mctool.intermediates.SenderDataChangedEvent;
import com.spam.mctool.model.ReceiverAddedOrRemovedListener;
import com.spam.mctool.model.ReceiverDataChangeListener;
import com.spam.mctool.model.SenderAddedOrRemovedListener;
import com.spam.mctool.model.SenderDataChangeListener;
import javax.swing.UIManager;

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
	private StreamManager streamManager;
        private ProfileManager profileManager;

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

        // TODO nobody calls this method so far
	public void dataChanged(ReceiverDataChangedEvent e) {
		// TODO Auto-generated method stub

	}

        // TODO nobody calls this method so far
	public void dataChanged(SenderDataChangedEvent e) {
		// TODO Auto-generated method stub
	}

	/**
	 * Initializes the graphical user interface and displays the main window.
	 */
	public void init(Controller c) {
		// TODO Auto-generated method stub
                streamManager = c;
                profileManager = c;

                // Set System L&F. Will only work, if the application did not
                // yet reference any other swing component!
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
		mainFrame = new MainFrame();
                loadState();
                //attachObservers(); // Doesn't work yet, beacause the
                                     // doesn't instantiate a sender and receiver
                                     // pool yet.
		mainFrame.setVisible(true);
	}

    private void loadState() {
        // TODO load the GUI state from the file and set it to the frame

        // fire the update methods to load all model data to the gui
        profileChanged(new ProfileChangeEvent());
        receiverAdded(new ReceiverAddedOrRemovedEvent());
        senderAdded(new SenderAddedOrRemovedEvent());
    }

    private void saveState() {
        // TODO save the relevant GUI state to a file
    }

    // Note, that Observers to Senders and Receivers are already added during
    // senderAdded and receiverAdded calls.
    private void attachObservers() {
        streamManager.addSenderAddedOrRemovedListener(this);
        streamManager.addReceiverAddedOrRemovedListener(this);
        profileManager.addProfileChangeListener(this);
    }

}
