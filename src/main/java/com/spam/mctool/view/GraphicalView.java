package com.spam.mctool.view;

import com.spam.mctool.controller.ErrorEvent;
import com.spam.mctool.controller.ErrorEventListener;
import com.spam.mctool.intermediates.OverallReceiverStatisticsUpdatedEvent;
import com.spam.mctool.view.dialogs.PreferencesDialog;
import com.spam.mctool.view.main.MainFrame;
import com.spam.mctool.controller.Controller;
import com.spam.mctool.controller.ErrorEventManager;
import com.spam.mctool.controller.LanguageChangeListener;
import com.spam.mctool.controller.LanguageManager;
import com.spam.mctool.controller.Profile;
import com.spam.mctool.controller.ProfileChangeListener;
import com.spam.mctool.controller.ProfileManager;
import com.spam.mctool.controller.StreamManager;
import com.spam.mctool.intermediates.OverallSenderStatisticsUpdatedEvent;
import com.spam.mctool.intermediates.ProfileChangeEvent;
import com.spam.mctool.intermediates.ReceiverAddedOrRemovedEvent;
import com.spam.mctool.intermediates.ReceiverDataChangedEvent;
import com.spam.mctool.intermediates.SenderAddedOrRemovedEvent;
import com.spam.mctool.intermediates.SenderDataChangedEvent;
import com.spam.mctool.model.MulticastStream;
import com.spam.mctool.model.OverallReceiverStatisticsUpdatedListener;
import com.spam.mctool.model.OverallSenderStatisticsUpdatedListener;
import com.spam.mctool.model.ReceiverAddedOrRemovedListener;
import com.spam.mctool.model.ReceiverDataChangeListener;
import com.spam.mctool.model.ReceiverGroup;
import com.spam.mctool.model.Sender;
import com.spam.mctool.model.SenderAddedOrRemovedListener;
import com.spam.mctool.model.SenderDataChangeListener;
import com.spam.mctool.view.dialogs.ErrorDialog;
import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import javax.swing.UIManager;

/**
 * Represents the graphical user interface of the MCTool.
 *
 * This class serves as the entry point for the controller to the view. All
 * incoming events are queued to the EventDispatcherThread here and thus
 * distributed safely to the remaining Swing components.
 *
 * In order to fully initialize and show the view, the <code>init</code>-method has to
 * be invoked.
 *
 * @author Tobias Stöckel
 *
 */
public class GraphicalView implements MctoolView,
                                      SenderDataChangeListener,
                                      ReceiverDataChangeListener,
                                      ProfileChangeListener,
                                      SenderAddedOrRemovedListener,
                                      ReceiverAddedOrRemovedListener,
                                      ErrorEventListener,
                                      OverallReceiverStatisticsUpdatedListener,
                                      OverallSenderStatisticsUpdatedListener,
                                      LanguageChangeListener {

    // <editor-fold desc="private attributes">
    /**
     * Reference to the main frame of the application
     */
    private MainFrame mainFrame;
    /**
     * Reference to the controller, seen as manager of multicast streams
     */
    private StreamManager streamManager;
    /**
     * Reference to the controller, seen as manager of streaming profiles
     */
    private ProfileManager profileManager;
    /**
     * Reference to the controller, seen as manager of error events
     */
    private ErrorEventManager errorEventManager;
    /**
     * Reference to the controller, seen as manager of program language
     */
    private LanguageManager languageManager;
    /**
     * Reference to the controller. This reference is only needed for
     * invoking the exitApplication()-method.
     */
    private Controller controller;
    /**
     * The Error Dialog that is reused for displaying error events
     */
    private ErrorDialog errorDialog;

    // </editor-fold>


    // <editor-fold desc="public methods">
    /**
     * Initializes the graphical user interface and displays the main window.
     * This method will also try to set the current operating system's
     * look-and-feel (L&F). This will only succeed if the application doesn't
     * already reference any other swing components.
     */
    public void init(Controller c) {

        streamManager     = c;
        profileManager    = c;
        errorEventManager = c;
        languageManager   = c;
        controller        = c;

        // Set System L&F. Will only work, if the application did not
        // yet reference any other swing component!
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Failed to set system Look and Feel. Defaulting to Java Look and Feel.");
        }

        // create the main frame and error dialog
        mainFrame = new MainFrame(this);
        errorDialog = new ErrorDialog(mainFrame, true);

        // bulk load existing receivers and senders to the view by simulating
        // sender and receiver addition events. This will also automatically
        // register the view as listener to all senders and receivers found in
        // the model.
        profileChanged(new ProfileChangeEvent());
        for (ReceiverGroup r : streamManager.getReceiverGroups()) {
            this.receiverGroupAdded(new ReceiverAddedOrRemovedEvent(r));
        }
        for (Sender s : streamManager.getSenders()) {
            this.senderAdded(new SenderAddedOrRemovedEvent(s));
        }

        // register the view as listener to the controller's functionality
        streamManager.addSenderAddedOrRemovedListener(this);
        streamManager.addReceiverAddedOrRemovedListener(this);
        streamManager.addOverallReceiverStatisticsUpdatedListener(this);
        streamManager.addOverallSenderStatisticsUpdatedListener(this);
        profileManager.addProfileChangeListener(this);
        languageManager.addLanguageChangeListener(this);
        errorEventManager.addErrorEventListener(this, ErrorEventManager.WARNING);

        // check if there is already a profile loaded
        if(profileManager.getCurrentProfile() != null) {
            this.profileChanged(new ProfileChangeEvent());
        }

        // display the main frame
        mainFrame.setVisible(true);
    }



    public void receiverGroupAdded(final ReceiverAddedOrRemovedEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mainFrame.receiverGroupAdded(e);
            }
        });

        // This call should be safe without synchronization with the GUI
        // because all incoming events that result from this registration
        // will themselves be enqueued to the EventDispatcherThread.
        e.getSource().addReceiverDataChangeListener(this);
    }



    public void receiverGroupRemoved(final ReceiverAddedOrRemovedEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mainFrame.receiverGroupRemoved(e);
            }
        });
    }



    public void senderAdded(final SenderAddedOrRemovedEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mainFrame.senderAdded(e);
            }
        });

        // This call should be safe without synchronization with the GUI
        // because all incoming events that result from this registration
        // will themselves be enqueued to the EventDispatcherThread.
        e.getSource().addSenderDataChangeListener(this);
    }



    public void senderRemoved(final SenderAddedOrRemovedEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mainFrame.senderRemoved(e);
            }
        });
    }



    public void profileChanged(final ProfileChangeEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mainFrame.profileChanged(e);
            }
        });
    }



    public void dataChanged(final ReceiverDataChangedEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mainFrame.dataChanged(e);
            }
        });
    }



    public void dataChanged(final SenderDataChangedEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mainFrame.dataChanged(e);
            }
        });
    }// </editor-fold>


    

    /**
     * Adds a sender to this view's controller and optionally activates it
     * afterwards. Provides feedback, whether the operation was successful or not.
     * More information about why the creation has failed will be communicated
     * via ErrorEvents.
     * @param senderMap Map containing parameters for the sender to be created.
     * The map's contents are specified in SenderManager.
     * @param activate Shall the sender be activated after creation?
     * @return True, if the sender was created successfully. False, if the Sender
     * could not be created.
     */
    public boolean addSender(Map<String, String> senderMap, boolean activate) {
        Sender s = this.streamManager.addSender(senderMap);
        if(s != null) {
            if(activate) s.activate();
            return true;
        } else {
            return false;
        }
    }



    /**
     * Adds a receiver group to this view's controller and optionally activates it
     * afterwards. Provides feedback, whether the operation was successful or not.
     * More information about why the creation has failed will be communicated
     * via ErrorEvents.
     * @param receiverMap Map containing parameters for the receiver group to be created.
     * The map's contents are specified in ReceiverManager.
     * @param activate Shall the receiver group be activated after creation?
     * @return True, if the receiver group was created successfully. False, if
     * rhe receiver group could not be created.
     */
    public boolean addReceiverGroup(Map<String, String> receiverMap, boolean activate) {
        ReceiverGroup r = this.streamManager.addReceiverGroup(receiverMap);
        if(r != null) {
            if(activate) r.activate();
            return true;
        } else {
            return false;
        }
    }



    /**
     * Forwards a request for stream removal to the controller.
     * @param streams the streams to be removed from the model
     */
    public void removeStreams(Set<MulticastStream> streams) {
        this.streamManager.removeStreams(streams);
    }



    /**
     * Allows the main frame to ask for the currently loaded profile.
     * @return the currently loaded profile as returned from the controller
     */
    public Profile getCurrentProfile() {
        return profileManager.getCurrentProfile();
    }



    /**
     * Forwards a request to load a specific profile to the controller
     * @param selectedFile the file to load the profile from
     */
    public void loadProfile(File selectedFile) {
        profileManager.loadProfile(selectedFile);
    }



    /**
     * Forwards a request to save a profile with a specific name to the controller.
     * @param profileName the descriptive name of the profile to be stored
     * @param path the location for saving the profile
     */
    public void saveProfile(String profileName, File path) {
        profileManager.saveProfile(new Profile(profileName, path));
    }



    /**
     * Forwards a request to the controller to update an existing profile on
     * disk with the current stream configuration.
     */
    public void saveCurrentProfile() {
        profileManager.saveCurrentProfile();
    }



    public void newErrorEvent(final ErrorEvent e) {
        new Thread(new Runnable() { public void run() {
            if(!errorDialog.isVisible()) {
                errorDialog.setVisible(true);
            }
        }}).start();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                errorDialog.dataChanged(e);
            }
        });
    }



    /**
     * Provides the list of recent profiles to the main frame.
     * @return
     */
    public Iterable<Profile> getRecentProfiles() {
        return profileManager.getRecentProfiles();
    }



    public void kill() {

        // unregister all controller listeners
        streamManager.removeSenderAddedOrRemovedListener(this);
        streamManager.removeReceiverAddedOrRemovedListener(this);
        streamManager.removeOverallReceiverStatisticsUpdatedListener(this);
        streamManager.removeOverallSenderStatisticsUpdatedListener(this);
        profileManager.removeProfileChangeListener(this);
        languageManager.removeLanguageChangeListener(this);
        errorEventManager.removeErrorEventListener(this);

        // unregister all model listeners
        for(Sender s : streamManager.getSenders()) {
            s.removeSenderDataChangeListener(this);
        }
        for(ReceiverGroup rg : streamManager.getReceiverGroups()) {
            rg.removeReceiverDataChangeListener(this);
        }

        // dispose the main frame
        mainFrame.dispose();
    }



    public void overallReceiverStatisticsUpdated(OverallReceiverStatisticsUpdatedEvent e) {
        mainFrame.overallReceiverStatisticsUpdated(e);
    }



    public void overallSenderStatisticsUpdated(OverallSenderStatisticsUpdatedEvent e) {
        mainFrame.overallSenderStatisticsUpdated(e);
    }



    public void languageChanged() {
        kill();
        init(controller);
        mainFrame.setVisible(true);
    }



    public void exitApplication() {
        controller.exitApplication();
    }



    /**
     * Applies the preferences chosen by the user to the application.
     * At the moment user preferences will not be persisted to disk.
     * @param dlg
     */
    public void setPreferences(PreferencesDialog dlg) {
        Locale.setDefault(dlg.getSelectedLocale());

        // unregister from the controller before reporting the language change
        // in order to avoid concurrent modification of the listener list
        kill();
        
        languageManager.reportLanguageChange();

        // reinitialize the gui
        init(controller);
        mainFrame.setVisible(true);
    }

}
