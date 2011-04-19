/**
 * 
 */
package com.spam.mctool.view;

import com.spam.mctool.view.main.MainFrame;
import com.spam.mctool.controller.Controller;
import com.spam.mctool.controller.Profile;
import com.spam.mctool.controller.ProfileChangeListener;
import com.spam.mctool.controller.ProfileManager;
import com.spam.mctool.controller.StreamManager;
import com.spam.mctool.intermediates.ProfileChangeEvent;
import com.spam.mctool.intermediates.ReceiverAddedOrRemovedEvent;
import com.spam.mctool.intermediates.ReceiverDataChangedEvent;
import com.spam.mctool.intermediates.SenderAddedOrRemovedEvent;
import com.spam.mctool.intermediates.SenderDataChangedEvent;
import com.spam.mctool.model.MulticastStream;
import com.spam.mctool.model.ReceiverAddedOrRemovedListener;
import com.spam.mctool.model.ReceiverDataChangeListener;
import com.spam.mctool.model.ReceiverGroup;
import com.spam.mctool.model.Sender;
import com.spam.mctool.model.SenderAddedOrRemovedListener;
import com.spam.mctool.model.SenderDataChangeListener;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.swing.SwingUtilities;

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

	public void receiverGroupAdded(final ReceiverAddedOrRemovedEvent e) {
            Runnable groupAddedRunnable = new Runnable() {
                public void run() {mainFrame.receiverGroupAdded(e);}
            };
            SwingUtilities.invokeLater(groupAddedRunnable);

            // This call should be safe without synchronization with the GUI
            // because all incoming events that result from this registration
            // will themselves be enqueued to the EventDispatcherThread.
            e.getSource().addReceiverDataChangeListener(this);
	}

        public void receiverGroupRemoved(final ReceiverAddedOrRemovedEvent e) {
            Runnable groupRemovedRunnable = new Runnable() {
                public void run() {mainFrame.receiverGroupRemoved(e);}
            };
            SwingUtilities.invokeLater(groupRemovedRunnable);
	}

	public void senderAdded(final SenderAddedOrRemovedEvent e) {
            Runnable senderAddedRunnable = new Runnable() {
                public void run() {mainFrame.senderAdded(e);}
            };
            SwingUtilities.invokeLater(senderAddedRunnable);
            
            // This call should be safe without synchronization with the GUI
            // because all incoming events that result from this registration 
            // will themselves be enqueued to the EventDispatcherThread.
            e.getSource().addSenderDataChangeListener(this);
	}

	public void senderRemoved(final SenderAddedOrRemovedEvent e) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {mainFrame.senderRemoved(e);}
            });
	}

	public void profileChanged(final ProfileChangeEvent e) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() { mainFrame.profileChanged(e); }
            });
	}

        public void dataChanged(final ReceiverDataChangedEvent e) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() { mainFrame.dataChanged(e); }
            });
	}

        public void dataChanged(final SenderDataChangedEvent e) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {mainFrame.dataChanged(e);}
            });
	}

	/**
	 * Initializes the graphical user interface and displays the main window.
	 */
	public void init(Controller c) {

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
            loadState();
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
    }

    public void addSender(Map<String, String> senderMap, boolean activate) {
        Sender s = this.streamManager.addSender(senderMap);
        if(activate) s.activate();
    }

    public void addReceiver(Map<String, String> receiverMap, boolean activate) {
        ReceiverGroup r = this.streamManager.addReceiverGroup(receiverMap);
        if(activate) r.activate();
    }

    public void removeStreams(Set<MulticastStream> streams) {
        this.streamManager.removeStreams(streams);
    }

    public Collection<Sender> getSenders() {
        return this.streamManager.getSenders();
    }

    public Collection<ReceiverGroup> getReceiverGroups() {
        return this.streamManager.getReceiverGroups();
    }

    public Profile getCurrentProfile() {
        return profileManager.getCurrentProfile();
    }

}
