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
import com.spam.mctool.model.MulticastStream;
import com.spam.mctool.model.Receiver;
import com.spam.mctool.model.ReceiverAddedOrRemovedListener;
import com.spam.mctool.model.ReceiverDataChangeListener;
import com.spam.mctool.model.ReceiverGroup;
import com.spam.mctool.model.Sender;
import com.spam.mctool.model.SenderAddedOrRemovedListener;
import com.spam.mctool.model.SenderDataChangeListener;
import com.spam.mctool.view.main.receivertable.ReceiverTableModel;
import java.util.List;
import java.util.Map;

import java.util.Set;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

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
        /**
         * Model of the table representing the receivers in the view
         */
        private ReceiverTableModel receiverTable;

	// receiver oder receivergroup???
        public void receiverAdded(ReceiverAddedOrRemovedEvent e) {
            // ???
	}

        // receiverGroupRemoved???
	public void receiverRemoved(ReceiverAddedOrRemovedEvent e) {
            // ???
	}

	public void senderAdded(SenderAddedOrRemovedEvent e) {
            Sender s = e.getSource();
            mainFrame.senderAdded(s);
	}

	public void senderRemoved(SenderAddedOrRemovedEvent e) {
            Sender s = e.getSource();
            mainFrame.senderRemoved(s);
	}

	public void profileChanged(ProfileChangeEvent e) {
		// TODO Auto-generated method stub

	}

        // TODO nobody calls this method so far
	public void dataChanged(ReceiverDataChangedEvent e) {
            receiverTable.receiverDataChanged(e);
	}

        // TODO nobody calls this method so far
	public void dataChanged(SenderDataChangedEvent e) {
            Sender s = e.getSource();
            mainFrame.senderDataChanged(s);
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
                attachObservers(); // Doesn't work yet, beacause the Controller
                                     // doesn't instantiate a sender and receiver
                                     // pool yet.
		mainFrame.setVisible(true);
                loadTableTestData();
	}

    private void loadState() {
        // TODO load the GUI state from the file and set it to the frame

        // fire the update methods to load all model data to the gui
        profileChanged(new ProfileChangeEvent());
        for (Receiver r : streamManager.getReceivers()) {
            this.receiverAdded(new ReceiverAddedOrRemovedEvent(r));
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
        //streamManager.addReceiverAddedOrRemovedListener(this);
        //profileManager.addProfileChangeListener(this);
        //for (Receiver r : streamManager.getReceivers()) {
            // TODO howto register on receiver group?
        //}
        //for (Sender s : streamManager.getSenders()) {
        //    s.addSenderDataChangeListener(this);
        //}
    }

    private void loadTableTestData() {
    	// Bitte SenderManager nutzen
        //Sender s;
        //for(int i=0; i<20; i++) {
        //    s = new Sender();
        //    s.setPort(100 + i);
        //    this.senderAdded(new SenderAddedOrRemovedEvent(s));
        //}
    }

    public void addSender(Map<String, String> senderMap, boolean activate) {
        Sender s = this.streamManager.addSender(senderMap);
        s.addSenderDataChangeListener(this);
        s.activate();
    }

}
