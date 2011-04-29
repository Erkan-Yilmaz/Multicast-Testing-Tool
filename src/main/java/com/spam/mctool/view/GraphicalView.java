/**
 * 
 */
package com.spam.mctool.view;

import com.spam.mctool.controller.ErrorEvent;
import com.spam.mctool.controller.ErrorEventListener;
import com.spam.mctool.view.main.MainFrame;
import com.spam.mctool.controller.Controller;
import com.spam.mctool.controller.ErrorEventManager;
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
import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import javax.swing.UIManager;

/**
 * @author Tobias Schoknecht, Tobias Stöckel
 * 
 * Represents the graphical user interface of the MCTool.
 * 
 * This class serves as the entry point for the controller to the view. All
 * incoming events are queued to the EventDispatcherThread here and thus
 * distributed safely to the remaining Swing components.
 *
 * In order to fully initialize and show the view, the init()-method has to
 * be invoked.
 *
 */
public class GraphicalView implements MctoolView,
		SenderDataChangeListener, ReceiverDataChangeListener,
		ProfileChangeListener, SenderAddedOrRemovedListener,
		ReceiverAddedOrRemovedListener,
                ErrorEventListener {

	/**
         * Reference to the main frame of the application
         */
        private MainFrame mainFrame;

        /**
         * Reference to the controller, seen as manager of multicast streams
         */
        private StreamManager streamManager;

        /**
         * Reference ot the controller, seen as manager of streaming profiles
         */
        private ProfileManager profileManager;
        private ErrorEventManager errorEventManager;

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
         * This method will also try to set the current operating system's
         * look-and-feel (L&F). This will only succeed if the application doesn't
         * already reference any other swing components.
	 */
	public void init(Controller c) {

            streamManager = c;
            profileManager = c;
            errorEventManager = c;

            // Set System L&F. Will only work, if the application did not
            // yet reference any other swing component!
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.out.println("Failed to set system Look and Feel. Defaulting to Java Look and Feel.");
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
        errorEventManager.addErrorEventListener(this, ErrorEventManager.DEBUG);
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

    public void loadProfile(File selectedFile) {
        profileManager.loadProfile(selectedFile);
    }

    public void saveProfile(String profileName, File path) {
        profileManager.saveProfile(new Profile(profileName, path));
    }

    public void saveCurrentProfile() {
        profileManager.saveCurrentProfile();
    }

    public void newErrorEvent(ErrorEvent e) {
        String title;
        int messageType;
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("internationalization/Bundle");

        switch(e.getErrorLevel()) {
            case ErrorEventManager.DEBUG:
                title = bundle.getString("View.Error.debug.title");
                messageType = JOptionPane.INFORMATION_MESSAGE;
                break;
            case ErrorEventManager.WARNING:
                title = bundle.getString("View.Error.warning.title");
                messageType = JOptionPane.WARNING_MESSAGE;
                break;
            case ErrorEventManager.SEVERE:
                title = bundle.getString("View.Error.severe.title");
                messageType = JOptionPane.WARNING_MESSAGE;
                break;
            case ErrorEventManager.ERROR:
                title = bundle.getString("View.Error.error.title");
                messageType = JOptionPane.ERROR_MESSAGE;
                break;
            case ErrorEventManager.CRITICAL:
                title = bundle.getString("View.Error.critical.title");
                messageType = JOptionPane.ERROR_MESSAGE;
                break;
            case ErrorEventManager.FATAL:
                title = bundle.getString("View.Error.fatal.title");
                messageType = JOptionPane.ERROR_MESSAGE;
                break;
            default:
                title = bundle.getString("View.Error.unknown.title");
                messageType = JOptionPane.INFORMATION_MESSAGE;
                break;
        }

        JOptionPane.showMessageDialog (
                mainFrame,
                e.getCompleteMessage(),
                title,
                messageType
        );
    }

    public Iterable<Profile> getRecentProfiles() {
        return profileManager.getRecentProfiles();
    }

}
