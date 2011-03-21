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
	// TODO Add Dialogs
	private StreamManager streamManager;
        private ProfileManager profileManager;
        /**
         * Model of the table representing the senders in the view
         */
        private DefaultTableModel senderTable;
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
            if(getSenderRow(s) == -1) {
                senderTable.addRow (
                    new Object[] {
                        s,
                        s.getSenderId(),
                        s.getPort(),
                        s.getGroup(),
                        s.getSenderConfiguredPacketRate(),
                        /*
                        s.getMeasuredPacketRate(),
                        s.getAvgPacketRate(),
                        s.getMinPacketRate(),
                        s.getMaxPacketRate()
                        */
                        0,
                        0,
                        0,
                        0
                    }
                );
            } else {
                throw new RuntimeException("Sender " + s + " already added to table!");
            }
            s.addSenderDataChangeListener(this);
	}

	public void senderRemoved(SenderAddedOrRemovedEvent e) {
            Sender s = e.getSource();
            int senderRow = getSenderRow(s);
            if(senderRow > -1) {
                senderTable.removeRow(senderRow);
            } else {
                throw new RuntimeException("Sender " + s + " not found in table!");
            }

	}

	public void profileChanged(ProfileChangeEvent e) {
		// TODO Auto-generated method stub

	}

        // TODO nobody calls this method so far
	public void dataChanged(ReceiverDataChangedEvent e) {
            receiverTable.dataChanged(e);
	}

        // TODO nobody calls this method so far
	public void dataChanged(SenderDataChangedEvent e) {
            Sender s = e.getSource();
            int senderRow = getSenderRow(s);
            if(senderRow > -1) {
                senderTable.setValueAt(s, senderRow, 0);
                senderTable.setValueAt(s.getSenderId(), senderRow, 0);
                senderTable.setValueAt(s.getPort(), senderRow, 0);
                senderTable.setValueAt(s.getGroup(), senderRow, 0);
                senderTable.setValueAt(s.getSenderConfiguredPacketRate(), senderRow, 0);
                senderTable.setValueAt(s.getAvgPPS(), senderRow, 0);
                /*
                senderTable.setValueAt(s.getAvgPacketRate(), senderRow, 0);
                senderTable.setValueAt(s.getMinPacketRate(), senderRow, 0);
                senderTable.setValueAt(s.getMaxPacketRate(), senderRow, 0);
                */
                senderTable.setValueAt(s.getAvgPPS(), senderRow, 0);
                senderTable.setValueAt(s.getMinPPS(), senderRow, 0);
                senderTable.setValueAt(s.getMaxPPS(), senderRow, 0);
            }

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
                //loadState();
                //attachObservers(); // Doesn't work yet, beacause the Controller
                                     // doesn't instantiate a sender and receiver
                                     // pool yet.
                senderTable = mainFrame.getSenderTable();
		mainFrame.setVisible(true);
                loadTableTestData();
	}

    private void loadState() {
        // TODO load the GUI state from the file and set it to the frame

        // fire the update methods to load all model data to the gui
        profileChanged(new ProfileChangeEvent());
        for (ReceiverGroup r : streamManager.getReceivers()) {
            //this.receiverAdded(new ReceiverAddedOrRemovedEvent(r));
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
        for (ReceiverGroup r : streamManager.getReceivers()) {
            // TODO howto register on receiver group?
        }
        for (Sender s : streamManager.getSenders()) {
            s.addSenderDataChangeListener(this);
        }
    }

    private int getSenderRow(Sender s) {
        for(int i=0; i < senderTable.getRowCount(); i++) {
            if(senderTable.getValueAt(i, 0) == s) {
                // sender found in row i
                return i;
            }
        }
        // sender not found. returning -1
        return -1;
    }

    private int getReceiverRow(Receiver r) {
        for(int i=0; i < receiverTable.getRowCount(); i++) {
            if(receiverTable.getValueAt(i, 0) == r) {
                // sender found in row i
                return i;
            }
        }
        // sender not found. returning -1
        return -1;
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

}
