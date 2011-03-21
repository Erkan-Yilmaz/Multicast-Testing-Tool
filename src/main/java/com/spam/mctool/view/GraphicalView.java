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
import com.spam.mctool.model.ReceiverGroup;
import com.spam.mctool.model.Sender;
import com.spam.mctool.model.SenderAddedOrRemovedListener;
import com.spam.mctool.model.SenderDataChangeListener;
import java.util.Map;

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
	private StreamManager streamManager;
        private ProfileManager profileManager;

	// receiver oder receivergroup???
        public void receiverGroupAdded(ReceiverAddedOrRemovedEvent e) {
            mainFrame.receiverGroupAdded(e);
	}

        // receiverGroupRemoved???
	public void receiverGroupRemoved(ReceiverAddedOrRemovedEvent e) {
            mainFrame.receiverGroupRemoved(e);
	}

	public void senderAdded(SenderAddedOrRemovedEvent e) {
            mainFrame.senderAdded(e);
	}

	public void senderRemoved(SenderAddedOrRemovedEvent e) {
            mainFrame.senderRemoved(e);
	}

	public void profileChanged(ProfileChangeEvent e) {
		// TODO Auto-generated method stub

	}

        // TODO nobody calls this method so far
	public void dataChanged(ReceiverDataChangedEvent e) {
            mainFrame.dataChanged(e);
	}

        // TODO nobody calls this method so far
	public void dataChanged(SenderDataChangedEvent e) {
            mainFrame.dataChanged(e);
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
		mainFrame = new MainFrame(this);
                //loadState();
                attachObservers();
		mainFrame.setVisible(true);
	}

    private void loadState() {
        // TODO load the GUI state from the file and set it to the frame

        // fire the update methods to load all model data to the gui
        profileChanged(new ProfileChangeEvent());
        for (ReceiverGroup r : streamManager.getReceiverGroups()) {
            this.receiverGroupAdded(new ReceiverAddedOrRemovedEvent(r));
        }
        for (Sender s : streamManager.getSenders()) {
            this.senderAdded(new SenderAddedOrRemovedEvent(s));
        }
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
        //for (ReceiverGroup r : streamManager.getReceivers()) {
            // TODO howto register on receiver group?
        //}
        //for (Sender s : streamManager.getSenders()) {
        //    s.addSenderDataChangeListener(this);
        //}
    }

    public void addSender(Map<String, String> senderMap, boolean activate) {
        Sender s = this.streamManager.addSender(senderMap);
        s.addSenderDataChangeListener(this);
        if(activate) s.activate();
    }

    public void addReceiver(Map<String, String> receiverMap, boolean activate) {
        ReceiverGroup r = this.streamManager.addReceiverGroup(receiverMap);
        r.addReceiverDataChangeListener(this);
        if(activate) r.activate();
    }

}
