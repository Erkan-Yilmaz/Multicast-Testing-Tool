/**
 *
 */
package com.spam.mctool.controller;

import com.spam.mctool.model.OverallReceiverStatisticsUpdatedListener;
import com.spam.mctool.model.OverallSenderStatisticsUpdatedListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSSerializer;

import com.spam.mctool.intermediates.ProfileChangeEvent;
import com.spam.mctool.model.MulticastStream;
import com.spam.mctool.model.ReceiverAddedOrRemovedListener;
import com.spam.mctool.model.ReceiverGroup;
import com.spam.mctool.model.ReceiverManager;
import com.spam.mctool.model.ReceiverPool;
import com.spam.mctool.model.Sender;
import com.spam.mctool.model.SenderAddedOrRemovedListener;
import com.spam.mctool.model.SenderManager;
import com.spam.mctool.model.SenderPool;
import com.spam.mctool.view.CommandLineView;
import com.spam.mctool.view.GraphicalView;
import com.spam.mctool.view.MctoolView;

/**
 * @author David Hildenbrand
 *
 */
public final class Controller implements ProfileManager, StreamManager, ErrorEventManager, LanguageManager {

    /**
     *
     */
    private static Controller controller;
    /**
     *
     */
    private Profile currentProfile;
    /**
     *
     */
    private RecentProfiles recentProfiles;
    /**
     *
     */
    private List<ProfileChangeListener> profileChangeObservers;
    /**
    *
    */
    private List<LanguageChangeListener> languageChangeObservers;
    /**
     *
     */
    private List<ErrorEventListener> newErrorEventObservers;
    /**
     *
     */
    private Map<ErrorEventListener,Integer> newErrorEventObserversErrorLevel;
    /**
     *
     */
    private SenderManager senderPool;
    /**
     *
     */
    private ReceiverManager receiverPool;
    /**
     *
     */
    private List<MctoolView> viewers;

    private static String defaultActivationMode = "default";

    /**
     * The default constructor for the Controller.
     * Initiates all members.
     */
    Controller(){
        this.currentProfile = null;
        this.recentProfiles = new RecentProfiles();
        this.profileChangeObservers = new ArrayList<ProfileChangeListener>();
        this.languageChangeObservers = new ArrayList<LanguageChangeListener>();
        this.newErrorEventObservers = new ArrayList<ErrorEventListener>();
        this.newErrorEventObserversErrorLevel = new HashMap<ErrorEventListener, Integer>();
    }

    /**
     * Controller is a singleton.
     * @return the application controller
     */
    public static Controller getController() {
        if(controller == null) {
            controller = new Controller();
        }
        return controller;
    }

    /**
     * This is the entry point for the mctool.
     * It creates a new controller, initiates it and passes the program control to the controller.
     * @param args The parameters passed to the application
     */
    public static void main(String[] args) {
        controller = getController();
        controller.init(args);

    }

    /**
     * This method initiates the controller.
     * Parses the parameter, initiates the views and loads + starts profiles if defined in the argument list.
     * @param args The arguments passed to the application.
     */
    public void init(String[] args) {
        //Init the Sender and Receiver modules
        this.senderPool = new SenderPool();
        this.receiverPool = new ReceiverPool();
        //Create the views
        viewers = new ArrayList<MctoolView>();

        //try to load recent profiles
        try {
            this.loadRecentProfiles();
        } catch (FileNotFoundException e) {
            this.reportErrorEvent(new ErrorEvent(ErrorEventManager.DEBUG,"Controller.failedLoadingRecentProfiles.text",""));
        } catch (IOException e) {
            this.reportErrorEvent(new ErrorEvent(ErrorEventManager.DEBUG,"Controller.failedLoadingRecentProfiles2.text",""));
        }
        //Gui enabled by default
        boolean enableGui = true;
        //Cli disabled by default
        boolean enableCli = false;
        //The desired start mode for senders and receivers
        String startMode = defaultActivationMode;
        //The profiles to be loaded
        List<Profile> desiredProfiles = new ArrayList<Profile>();
        //iterate over all args
        for(int i = 0; i<args.length; ++i){
            //CLI desired?
            if(args[i].compareToIgnoreCase("-cli") == 0){
                enableCli = true;
            }
            //Disable the gui?
            else if(args[i].compareToIgnoreCase("-nogui") == 0){
                enableGui = false;
            }
            //load profiles by name?
            else if(args[i].compareToIgnoreCase("-profile") == 0){
                //iterate to the potential profile name
                ++i;
                //read the next argument if available
                if(i >= args.length || args[i].startsWith("-")){
                    //report the error
                    this.reportErrorEvent(new ErrorEvent(ErrorEventManager.FATAL,"Controller.missingArgumentProfileName.text",""));
                    //exit the program
                    return;
                }
                //read profile names until one name starts with "-" or there is nothing left
                do{
                    //try to find a profile by name
                    Profile profile = recentProfiles.findProfileByName(args[i]);
                    if(profile != null){
                        //profile found -> add it to the desired profile list
                        desiredProfiles.add(profile);
                    }
                    else{
                        //report the error
                        this.reportErrorEvent(new ErrorEvent(ErrorEventManager.SEVERE,"Controller.nameNotFoundInRecentProfiles.text", args[i]));
                    }
                    //iterate to next potential profile name
                    ++i;
                }while((i < args.length) && !args[i].startsWith("-"));
                --i;
            }
            //load profiles by path?
            else if(args[i].compareToIgnoreCase("-path") == 0){
                //iterate to the potential profile path
                ++i;
                //read the next argument if available
                if(i >= args.length || args[i].startsWith("-")){
                    //report the error
                    this.reportErrorEvent(new ErrorEvent(ErrorEventManager.FATAL,"Controller.missingArgumentProfilePath.text", ""));
                    //exit the program
                    return;
                }
                //read profile names until one name starts with "-" or there is nothing left
                do{
                    //try to find a profile by name
                    Profile profile = new Profile("Not loaded.",new File(args[i]));
                    //add it to the desired profile list
                    desiredProfiles.add(profile);
                    //iterate to next potential profile name
                    ++i;
                }while((i < args.length) && !args[i].startsWith("-"));
                --i;
            }
            //start all receivers and sender
            else if(args[i].compareToIgnoreCase("-startall") == 0){
                //start mode has already been set
                if(startMode.compareTo(defaultActivationMode) != 0){
                    //report the error
                    this.reportErrorEvent(new ErrorEvent(ErrorEventManager.FATAL, "Controller.falseStartmode.text", args[i]));
                    //exit the program
                    return;
                }
                startMode = "all";
            }
            //start none of the receivers and sender
            else if(args[i].compareToIgnoreCase("-startnone") == 0){
                //startmode has already been set
                if(startMode.compareTo(defaultActivationMode) != 0){
                    //report the error
                    this.reportErrorEvent(new ErrorEvent(ErrorEventManager.FATAL, "Controller.falseStartmode.text", args[i]));
                    //exit the program
                    return;
                }
                startMode = "none";
            }
            //restore the state of receivers and sender
            else if(args[i].compareToIgnoreCase("-restore") == 0){
                //startmode has already been set
                if(startMode.compareTo(defaultActivationMode) != 0){
                    //report the error
                    this.reportErrorEvent(new ErrorEvent(ErrorEventManager.FATAL, "Controller.falseStartmode.text", args[i]));
                    //exit the program
                    return;
                }
                startMode = "restore";
            }
            else{
                //report the error
                this.reportErrorEvent(new ErrorEvent(ErrorEventManager.FATAL, "Controller.unknownArgument.text", args[i]));
                //exit the program
                return;
            }

        }

        //enable the Gui
        if(enableGui){
            viewers.add(new GraphicalView());
        }
        //enable the cli/logger
        if(enableCli){
            viewers.add(new CommandLineView());
        }
        //Init all views
        for(MctoolView v:viewers){
            v.init(this);
        }

        //Try to load desired Profile
        if(desiredProfiles != null && desiredProfiles.size() > 0){
            int loadCount = 0;
            Profile loadedProfile = null;
            for(Profile p: desiredProfiles){
                try{
                    this.loadProfileWithoutCleanup(p,startMode);
                    loadCount++;
                    //add the profile to the recent profiles list
                    recentProfiles.addOrUpdateProfileInList(p);
                    loadedProfile = p;
                }
                catch(org.w3c.dom.ls.LSException e){
                    this.reportErrorEvent(new ErrorEvent(ErrorEventManager.ERROR,"Controller.profileLoadingError.text",p.getPath().toString() + ": " + e.getLocalizedMessage()));
                }
                catch(IOException e){
                    this.reportErrorEvent(new ErrorEvent(ErrorEventManager.ERROR,"Controller.profileLoadingError2.text", p.getPath().toString()));
                }
                catch(Exception e){
                    this.reportErrorEvent(new ErrorEvent(ErrorEventManager.CRITICAL,"Controller.profileLoadingError3.text",p.getPath().toString() + ": " + e.getLocalizedMessage()));
                }
            }
            //save the recent profiles list
            try {
                saveRecentProfiles();
            } catch (IOException e) {
                this.reportErrorEvent(new ErrorEvent(ErrorEventManager.ERROR,"Controller.failedSavingRecentProfiles.text",e.getLocalizedMessage()));
            }
            //No current profile for multiple profiles
            if(loadCount > ErrorEventManager.DEBUG){
                this.setCurrentProfile(null);
            }
            //Only one profile loaded? make it the active one
            else if(loadedProfile!=null){
                this.setCurrentProfile(loadedProfile);
            }
        }
    }

    /**
     * This method is called by the gui to exit the application.
     */
    public void exitApplication(){
        //Stop all senders and remove them
        if(senderPool != null){
            senderPool.killAll();
        }
        //Stop all receivers and remove them
        if(receiverPool != null){
            receiverPool.killAll();
        }
        //Write the recent profile list
        try {
            saveRecentProfiles();
        } catch (IOException e) {
            this.reportErrorEvent(new ErrorEvent(ErrorEventManager.ERROR,"Controller.failedSavingRecentProfiles.text",e.getLocalizedMessage()));
        }
        //Kill all views
        for(MctoolView v:viewers){
            v.kill();
        }
        //Exit the application
        System.exit(0);
    }

    /**
     * This method tries to save the profile list to the file named "RecentProfiles.xml"
     * @throws IOException If the recent profile list could not be saved.
     */
    private void saveRecentProfiles() throws IOException{
        //create a new file
        File recentProfilesPath = new File(System.getProperty("user.home"),"RecentProfiles.xml");
        //create a file output stream
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(recentProfilesPath);
        } catch (FileNotFoundException e) {
            // We can ignore this, file will be created
        }
        //Create a data output stream
        DataOutputStream dos = new DataOutputStream(fos);
        //Get the data from the object
        String xml = recentProfiles.toXML();
        //Write the list
        dos.writeUTF(xml);
        //close the dos stream
        dos.flush();
        dos.close();
        //close the fos
        fos.flush();
        fos.close();
    }

    /*
     * This method tries to load the profile list from the file named "RecentProfiles.xml"
     * @throws IOException if the file could not be found/red/parsed.
     */
    private void loadRecentProfiles() throws IOException{
        //create a new file object
        File recentProfilesPath = new File(System.getProperty("user.home"),"RecentProfiles.xml");
        //create a file input stream
        FileInputStream fis = new FileInputStream(recentProfilesPath);
        //create a data input stream
        DataInputStream dis = new DataInputStream(fis);
        //read the xml data
        String xml = dis.readUTF();
        //Import the data
        recentProfiles.fromXML(xml);
    }

    /**
     * This method informs all profileChanged observers about a changed profile.
     * The new profile can be retrieved by calling the getCurrentProfile method
     */
    private void profileChanged(){
        //Get the iterator for the observer list
        for(ProfileChangeListener l:profileChangeObservers){
            l.profileChanged(new ProfileChangeEvent());
        }
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.StreamManager#removeStreams(java.util.Collection)
     */
    public void removeStreams(Collection <? extends MulticastStream> streams) {
        //Fetch the set iterator
        for(MulticastStream m: streams){
            if(m instanceof Sender){
                senderPool.remove((Sender)m);
            }
            else if(m instanceof ReceiverGroup){
                receiverPool.remove((ReceiverGroup)m);
            }
            else{
                throw new IllegalArgumentException();
            }
        }
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.StreamManager#startStreams(java.util.Collection)
     */
    public void startStreams(Collection<? extends MulticastStream> streams) {
        //Fetch the set iterator
        for(MulticastStream m:streams){
            m.activate();
        }
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.StreamManager#stopStreams(java.util.Collection)
     */
    public void stopStreams(Collection<? extends MulticastStream> streams) {
        for(MulticastStream m:streams){
            m.deactivate();
        }
    }

    /*
     * This method will save the sender/receiver settings to the specified profile path
     * in currentProfile.
     * @param p The profile containing the path and
     * @throws Exception if an Error occured serializing the profile.
     */
    private void saveProfileToFile(Profile p) throws Exception {
        //Register the xerces DOM-Implementation
        DOMImplementationRegistry registry = null;
        registry = DOMImplementationRegistry.newInstance();

        //Load the xerces DOM implementation
        DOMImplementationLS impl =
            (DOMImplementationLS)registry.getDOMImplementation("LS");

        //Lets build a new factory for our XMLDocument
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        docBuilder = dbfac.newDocumentBuilder();
        //Create a XML DOM document
        Document xmlDocument = docBuilder.newDocument();

        //Create the root element which will be named "configuration" later on
        Element rootElement = xmlDocument.createElement("configuration");
        xmlDocument.appendChild(rootElement);

        //Profile section
        Element profileElement = xmlDocument.createElement("profile");
        rootElement.appendChild(profileElement);
        //Name Section in profile section
        Element profileNameElement = xmlDocument.createElement("name");
        profileElement.appendChild(profileNameElement);
        //The name as text
        Text profileNameText = xmlDocument.createTextNode(p.getName());
        profileNameElement.appendChild(profileNameText);
        //The Time Section
        Element profileCreationTimeElement = xmlDocument.createElement("creationTime");
        profileElement.appendChild(profileCreationTimeElement);
        //The time as text
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        Text profileCreationTimeText = xmlDocument.createTextNode(dateFormat.format(date));
        profileCreationTimeElement.appendChild(profileCreationTimeText);

        //Senders section
        Element sendersElement = xmlDocument.createElement("senders");
        rootElement.appendChild(sendersElement);

        //fetch all senders
        java.util.Collection<Sender> senders = getSenders();
        Iterator<Sender> itS = senders.iterator();
        //iterate over the collection

        while(itS.hasNext()){
            Sender curSender = itS.next();
            //Create sender element
            Element senderElement = xmlDocument.createElement("sender");
            sendersElement.appendChild(senderElement);
            //Fetch the senders configuration MAP
            Map<String, String> map = curSender.getConfiguration();
            //Fetch all keys
            Set<String> set = map.keySet();
            Iterator<String> it = set.iterator();
            while(it.hasNext()){
                //look up the value
                String key = it.next();
                String value = map.get(key);
                //Create a element for the key
                Element keyElement = xmlDocument.createElement(key);
                senderElement.appendChild(keyElement);
                //Add its value as text
                Text valueText = xmlDocument.createTextNode(value);
                keyElement.appendChild(valueText);
            }
            //save the active state
            Element keyElement = xmlDocument.createElement("active");
            senderElement.appendChild(keyElement);
            String state = "false";
            if(curSender.isActive()){
                state = "true";
            }
            Text valueText = xmlDocument.createTextNode(state);
            keyElement.appendChild(valueText);
        }

        //Receivers section
        Element receiversElement = xmlDocument.createElement("receivers");
        rootElement.appendChild(receiversElement);

        //fetch all receivers
        java.util.Collection<ReceiverGroup> receivers = getReceiverGroups();
        Iterator<ReceiverGroup> itR = receivers.iterator();
        //iterate over the collection
        while(itR.hasNext()){
            ReceiverGroup curReceiver = itR.next();
            //Create receiver element
            Element receiverElement = xmlDocument.createElement("receiver");
            receiversElement.appendChild(receiverElement);
            //Fetch the receiver configuration MAP
            Map<String, String> map = curReceiver.getConfiguration();
            //Fetch all keys
            Set<String> set = map.keySet();
            Iterator<String> it = set.iterator();
            while(it.hasNext()){
                //look up the value
                String key = it.next();
                String value = map.get(key);
                //Create a element for the key
                Element keyElement = xmlDocument.createElement(key);
                receiverElement.appendChild(keyElement);
                //Add its value as text
                Text valueText = xmlDocument.createTextNode(value);
                keyElement.appendChild(valueText);
            }
            //save the active state
            Element keyElement = xmlDocument.createElement("active");
            receiverElement.appendChild(keyElement);
            String state = "false";
            if(curReceiver.isActive()){
                state = "true";
            }
            Text valueText = xmlDocument.createTextNode(state);
            keyElement.appendChild(valueText);
        }

        //get the desired profile path
        File profilePath = p.getPath();

        //Our LSSerializer will create the XML output
        LSSerializer writer = impl.createLSSerializer();
        //Beautiful formatted output
        writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
        //Try to write it to the file
        writer.writeToURI(xmlDocument, profilePath.toURI().toString());
    }


    /**
     * @param p The profile containing the path to load the profile from.
     * @param startMode The start mode. Default is "default" or "restore" - Restores
     * the active state. "all" starts all streams and "none" starts no streams.
     * @throws org.w3c.dom.ls.LSException If there was a problem loading and parsing the profile file.
     * @throws IOException If the profile file could not be red.
     * @throws Exception If there was a problem initiating the dom environment.
     */
    public void loadProfileWithoutCleanup(Profile p,String startMode) throws org.w3c.dom.ls.LSException,IOException,Exception{
        if(p == null){
            throw new IllegalArgumentException();
        }
        if(startMode == null || startMode.compareToIgnoreCase("restore") == 0){
            startMode = defaultActivationMode;
        }

        DOMImplementationRegistry registry=null;
        registry = DOMImplementationRegistry.newInstance();

        DOMImplementationLS impl =
            (DOMImplementationLS)registry.getDOMImplementation("LS");

        //Create a parser object
        LSParser builder = impl.createLSParser(
            DOMImplementationLS.MODE_SYNCHRONOUS, null);

        //Diasble the printing of the error message to the log
        builder.getDomConfig().setParameter( "error-handler", null );

        //Fetch the profile path
        File xmlPath = p.getPath();
        //Parse the file
        Document xmlDocument = builder.parseURI(xmlPath.toString());
        //Find the profile section
        Node profile = xmlDocument.getElementsByTagName("profile").item(0);
        //fetch the child nodes
        NodeList profileChilds = profile.getChildNodes();
        for(int i = 0;i<profileChilds.getLength(); ++i){
            Node curNode = profileChilds.item(i);
            if(curNode.getNodeName().compareToIgnoreCase("name") == 0){
                String name = curNode.getTextContent();
                if(name == null){
                    name = "";
                }
                //set the name of the profile
                p.setName(name);
            }
        }
        //Find the senders section
        Node senders = xmlDocument.getElementsByTagName("senders").item(0);
        //fetch the child nodes
        NodeList sendersChilds = senders.getChildNodes();
        for(int i = 0;i<sendersChilds.getLength(); ++i){
            Node curSender = sendersChilds.item(i);
            //check if it is really a sender
            if(curSender.getNodeName().compareToIgnoreCase("sender") == 0){
                //start the sender
                Boolean startSender = false;
                //fetch all nodes
                NodeList senderData = curSender.getChildNodes();
                //create a map
                Map<String,String> map = new HashMap<String, String>();
                for(int j = 0;j<senderData.getLength();++j){
                    Node curData = senderData.item(j);
                    if(curData.getNodeName().compareToIgnoreCase("active") == 0){
                        //marked to be activated
                        if(curData.getTextContent().compareToIgnoreCase("true") == 0){
                            startSender = true;
                        }
                        //marked to not be activated
                        else if(curData.getTextContent().compareToIgnoreCase("false") == 0){
                            startSender = false;
                        }
                        //unknown definition
                        else{
                            startSender = false;
                            this.reportErrorEvent(new ErrorEvent(ErrorEventManager.DEBUG,"Controller.profileActiveModeError.text", null));
                        }
                    }
                    else{
                        //put the pair to the map
                        map.put(curData.getNodeName(), curData.getTextContent());
                    }
                }
                //Add the sender
                Sender newSender = addSender(map);
                //if startMode=none -> Start none
                //otherwise test if all should be started or the stream is marked to be started
                if(newSender != null && startMode.compareToIgnoreCase("none") != 0 && (startMode.compareToIgnoreCase("all") == 0 || startSender)){
                    newSender.activate();
                }
            }
        }

        //Find the receiver section
        Node receivers = xmlDocument.getElementsByTagName("receivers").item(0);
        //fetch the child nodes
        NodeList receiversChilds = receivers.getChildNodes();
        for(int i = 0;i<receiversChilds.getLength(); ++i){
            Node curReceiver = receiversChilds.item(i);
            //check if it is really a sender
            if(curReceiver.getNodeName().compareToIgnoreCase("receiver") == 0){
                //start the receiver
                Boolean startReceiver = false;
                //fetch all nodes
                NodeList receiverData = curReceiver.getChildNodes();
                //create a map
                Map<String,String> map = new HashMap<String, String>();
                for(int j = 0;j<receiverData.getLength();++j){
                    Node curData = receiverData.item(j);
                    if(curData.getNodeName().compareToIgnoreCase("active") == 0){
                        //marked to be activated
                        if(curData.getTextContent().compareToIgnoreCase("true") == 0){
                            startReceiver = true;
                        }
                        //marked to not be activated
                        else if(curData.getTextContent().compareToIgnoreCase("false") == 0){
                            startReceiver = false;
                        }
                        //unknown definition
                        else{
                            startReceiver = false;
                            this.reportErrorEvent(new ErrorEvent(ErrorEventManager.DEBUG,"Controller.profileActiveModeError.text", null));
                        }
                    }
                    else{
                        //put the pair to the map
                        map.put(curData.getNodeName(), curData.getTextContent());
                    }
                }
                //Add the sender
                ReceiverGroup newReceiver = addReceiverGroup(map);
                //if startMode=nix dasone -> Start none
                //otherwise test if all should be started or the stream is marked to be started
                if(newReceiver != null && startMode.compareToIgnoreCase("none") != 0 && (startMode.compareToIgnoreCase("all") == 0 || startReceiver)){
                    newReceiver.activate();
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.StreamManager#addSender(java.util.Map)
     */
    public Sender addSender(Map<String, String> params) {
        return this.senderPool.create(params);
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.StreamManager#addReceiverGroup(java.util.Map)
     */
    public ReceiverGroup addReceiverGroup(Map<String, String> params) {
        return receiverPool.create(params);
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.StreamManager#getSenders()
     */
    public Collection<Sender> getSenders() {
        return senderPool.getSenders();
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.StreamManager#getReceiverGroups()
     */
    public Collection<ReceiverGroup> getReceiverGroups() {
        return receiverPool.getReceiverGroups();
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.StreamManager#addSenderAddedOrRemovedListener(com.spam.mctool.model.SenderAddedOrRemovedListener)
     */
    public void addSenderAddedOrRemovedListener(SenderAddedOrRemovedListener l) {
        senderPool.addSenderAddedOrRemovedListener(l);
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.StreamManager#removeSenderAddedOrRemovedListener(com.spam.mctool.model.SenderAddedOrRemovedListener)
     */
    public void removeSenderAddedOrRemovedListener(
            SenderAddedOrRemovedListener l) {
        senderPool.removeSenderAddedOrRemovedListener(l);
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.StreamManager#addReceiverAddedOrRemovedListener(com.spam.mctool.model.ReceiverAddedOrRemovedListener)
     */
    public void addReceiverAddedOrRemovedListener(
            ReceiverAddedOrRemovedListener l) {
        receiverPool.addReceiverAddedOrRemovedListener(l);
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.StreamManager#removeReceiverAddedOrRemovedListener(com.spam.mctool.model.ReceiverAddedOrRemovedListener)
     */
    public void removeReceiverAddedOrRemovedListener(
            ReceiverAddedOrRemovedListener l) {
        receiverPool.removeReceiverAddedOrRemovedListener(l);
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.ProfileManager#saveProfile(com.spam.mctool.controller.Profile)
     */
    public void saveProfile(Profile p) {
        if(p == null){
            throw new IllegalArgumentException();
        }
        try {
            saveProfileToFile(p);
        } catch (Exception e) {
            this.reportErrorEvent(new ErrorEvent(ErrorEventManager.ERROR,"Controller.profileSavingError.text",p.getPath() + ": " + e.getLocalizedMessage()));
            return;
        }
        //refresh the profile name and path
        this.setCurrentProfile(p);
        //if successfully saved, add or update it to the list of recent profiles
        recentProfiles.addOrUpdateProfileInList(p);

        //save the recent profile list
        try {
            saveRecentProfiles();
        } catch (IOException e) {
            this.reportErrorEvent(new ErrorEvent(ErrorEventManager.ERROR,"Controller.failedSavingRecentProfiles.text",e.getLocalizedMessage()));
        }
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.ProfileManager#saveCurrentProfile()
     */
    public void saveCurrentProfile(){
        //if we have no current profile, report an error
        if(this.currentProfile == null){
            this.reportErrorEvent(new ErrorEvent(ErrorEventManager.ERROR,"Controller.noCurrentProfile.text",null));
        }
        else{
            saveProfile(this.currentProfile);
        }
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.ErrorEventManager#addErrorEventListener(com.spam.mctool.controller.ErrorEventListener, int)
     */
    @Override
    public void addErrorEventListener(ErrorEventListener l, int errorLevel) {
        //the listener must not be null
        if(l == null){
            throw new IllegalArgumentException();
        }
        //make sure that the error level is in the allowed range
        if(errorLevel < 0){
            errorLevel = 0;
        }
        else if(errorLevel > ErrorEventManager.FATAL){
            errorLevel = ErrorEventManager.FATAL;
        }
        //if it is already contained in the list, remove the mapping and remap
        else if(this.newErrorEventObservers.contains(l)){
            this.newErrorEventObserversErrorLevel.remove(l);
            this.newErrorEventObserversErrorLevel.put(l, errorLevel);
        }
        //Add the observer to the list and the map
        else{
            this.newErrorEventObservers.add(l);
            this.newErrorEventObserversErrorLevel.put(l, errorLevel);
        }
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.ErrorEventManager#removeErrorEventListener(com.spam.mctool.controller.ErrorEventListener)
     */
    @Override
    public void removeErrorEventListener(ErrorEventListener l) {
        //the listener must not be null
        if(l == null){
            throw new IllegalArgumentException();
        }
        //remove it from the list and the map
        this.newErrorEventObservers.remove(l);
        this.newErrorEventObserversErrorLevel.remove(l);
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.ErrorEventManager#reportErrorEvent(com.spam.mctool.controller.ErrorEvent)
     */
    @Override
    public void reportErrorEvent(ErrorEvent e) {
        //the event must not be null
        if(e == null){
            throw new IllegalArgumentException();
        }
        //fetch the errorLevel from the event
        int errorLevel = e.getErrorLevel();
        //if there are no listener, print it to stdout
        if(newErrorEventObservers.size() <= 0){
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            System.out.println(dateFormat.format(date) + ": Level " + errorLevel + " :" + e.getCompleteMessage());

        }
        //iterate over all listener
        for(ErrorEventListener l: newErrorEventObservers){
            //The listener will only be called if the event error level is higher or equal to the desired level
            if(this.newErrorEventObserversErrorLevel.get(l) <= errorLevel){
                l.newErrorEvent(e);
            }
        }

    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.ProfileManager#addProfileChangeListener(com.spam.mctool.controller.ProfileChangeListener)
     */
    public void addProfileChangeListener(ProfileChangeListener listener) {
        this.profileChangeObservers.add(listener);
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.ProfileManager#removeProfileChangeListener(com.spam.mctool.controller.ProfileChangeListener)
     */
    public void removeProfileChangeListener(ProfileChangeListener listener) {
        this.profileChangeObservers.remove(listener);
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.ProfileManager#getCurrentProfile()
     */
    public Profile getCurrentProfile() {
        return currentProfile;
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.ProfileManager#loadProfile(java.io.File)
     */
    public void loadProfile(File path){
        //path must not be null
        if(path == null){
            throw new IllegalArgumentException();
        }
        //First of all delete all senders and receivers
        removeStreams(getReceiverGroups());
        removeStreams(getSenders());

        //Create a temporary profile
        Profile p = new Profile("Not loaded.",path);

        //Load the profile, streams are reverted to the saved activity state
        try{
            this.loadProfileWithoutCleanup(p,defaultActivationMode);
            //Add it to the list of recent profiles
            recentProfiles.addOrUpdateProfileInList(p);
            //Make it the new profile and signalize it to the observers
            this.setCurrentProfile(p);
        }
        catch(org.w3c.dom.ls.LSException e){
            this.reportErrorEvent(new ErrorEvent(ErrorEventManager.ERROR,"Controller.profileLoadingError.text",p.getPath().toString() + ": " + e.getLocalizedMessage()));
            //On error the profile will be set to 0
            this.setCurrentProfile(null);
        }
        catch(IOException e){
            this.reportErrorEvent(new ErrorEvent(ErrorEventManager.ERROR,"Controller.profileLoadingError2.text", p.getPath().toString()));
            //On error the profile will be set to 0
            this.setCurrentProfile(null);
        }
        catch(Exception e){
            this.reportErrorEvent(new ErrorEvent(ErrorEventManager.CRITICAL,"Controller.profileLoadingError3.text",p.getPath().toString() + ": " + e.getLocalizedMessage()));
            //On error the profile will be set to 0
            this.setCurrentProfile(null);
        }

    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.ProfileManager#setCurrentProfile(com.spam.mctool.controller.Profile)
     */
    public void setCurrentProfile(Profile currentProfile) {
        //This will be our new profile
        this.currentProfile = currentProfile;
        //Inform observers
        profileChanged();
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.ProfileManager#getRecentProfiles()
     */
    public List<Profile> getRecentProfiles() {
        return recentProfiles.getProfileList();
    }

    @Override
    public void addLanguageChangeListener(LanguageChangeListener l) {
        if(l==null){
            throw new IllegalArgumentException();
        }
        //add it to the list
        languageChangeObservers.add(l);
    }

    @Override
    public void removeLanguageChangeListener(LanguageChangeListener l) {
        if(l==null){
            throw new IllegalArgumentException();
        }
        //remove it from the list
        languageChangeObservers.remove(l);
    }

    @Override
    public void reportLanguageChange() {
        for(LanguageChangeListener li:languageChangeObservers){
            li.languageChanged();
        }

    }

    public void addOverallReceiverStatisticsUpdatedListener(OverallReceiverStatisticsUpdatedListener l) {
        receiverPool.addOverallReceiverStatisticsUpdatedListener(l);
    }

    public void addOverallSenderStatisticsUpdatedListener(OverallSenderStatisticsUpdatedListener l) {
        senderPool.addOverallSenderStatisticsUpdatedListener(l);
    }


    @Override
    public void removeOverallReceiverStatisticsUpdatedListener(
            OverallReceiverStatisticsUpdatedListener l) {
        if(l == null){
            throw new IllegalArgumentException();
        }
        receiverPool.removeOverallReceiverStatisticsUpdatedListener(l);
    }

    @Override
    public void removeOverallSenderStatisticsUpdatedListener(
            OverallSenderStatisticsUpdatedListener l) {
        if(l == null){
            throw new IllegalArgumentException();
        }
        senderPool.removeOverallSenderStatisticsUpdatedListener(l);
    }


}
