/**
 *
 */
package com.spam.mctool.controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSException;
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
import com.thoughtworks.xstream.XStream;

/**
 * @author davidhildenbrand
 *
 */
public class Controller implements ProfileManager, StreamManager, ErrorEventManager {

    private static Controller controller;
    private Profile currentProfile;
    private RecentProfiles recentProfiles;
    private List<ProfileChangeListener> profileChangeObservers;
    private List<ErrorEventListener> newErrorEventObservers;
    private Map<ErrorEventListener,Integer> newErrorEventObserversErrorLevel;
    private SenderManager senderPool;
    private ReceiverManager receiverPool;
    private List<MctoolView> viewers;


    private Controller(){
        this.currentProfile = new Profile();
        this.recentProfiles = new RecentProfiles();
        this.profileChangeObservers = new ArrayList<ProfileChangeListener>();
        this.newErrorEventObservers = new ArrayList<ErrorEventListener>();
        this.newErrorEventObserversErrorLevel = new HashMap<ErrorEventListener, Integer>();
        //Init the Sender and Receiver modules
        this.senderPool = new SenderPool();
        this.receiverPool = new ReceiverPool();
        //Create the vies
        viewers = new ArrayList<MctoolView>();
    }

    /*
     * This function tries to save the profile list to the file named "RecentProfiles.xml"
     */
    private void saveRecentProfiles() throws IOException{
        //create a new file
        File recentProfilesPath = new File("RecentProfiles.xml");
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
     * This function tries to load the profile list from the file named "RecentProfiles.xml"
     */
    private void loadRecentProfiles() throws IOException{
        //create a new file
        File recentProfilesPath = new File("RecentProfiles.xml");
        //create a file input stream
        FileInputStream fis = new FileInputStream(recentProfilesPath);
        //create a data input stream
        DataInputStream dis = new DataInputStream(fis);
        //read the xml data
        String xml = dis.readUTF();
        //Import the data
        recentProfiles.fromXML(xml);
    }

    private void profileChanged(){
        //Get the iterator for the observer list
        ListIterator<ProfileChangeListener> it = profileChangeObservers.listIterator();
        for(ProfileChangeListener l:profileChangeObservers){
        	l.profileChanged(new ProfileChangeEvent());
        }
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }

    public void init(String[] args) {
            //try to load recent profiles
            try {
                this.loadRecentProfiles();
            } catch (FileNotFoundException e) {
                this.reportErrorEvent(new ErrorEvent("The recent profiles list could not be read. File not found.",0));
            } catch (IOException e) {
                this.reportErrorEvent(new ErrorEvent("The recent profiles list could not be read. File could not be opened.",2));
            }
            //Gui enabled by default
            boolean enableGui = true;
            //Cli disabled by default
            boolean enableCli = false;
            //Start all loaded senders and receivers later?
            boolean enableStartAll = false;
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
                    	this.reportErrorEvent(new ErrorEvent("The supplied list of arguments is not correct. Please define one or more names of recent profiles.", 5));
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
                        	this.reportErrorEvent(new ErrorEvent("The profile with the name \"" + args[i] + "\" could not be found in the recent profile history.", 1));
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
                    	this.reportErrorEvent(new ErrorEvent("The supplied list of arguments is not correct. Please define one or more paths to profiles.", 5));
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
                    enableStartAll = true;
                }
                else{
                    //report the error
                	this.reportErrorEvent(new ErrorEvent("Unknown parameter: " + args[i], 5));
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
                //viewers.add(new CommandLineView());
            }
            //Init all views
            Iterator<MctoolView> it = viewers.iterator();
            for(MctoolView v:viewers){
            	v.init(this);
            }

            //Try to load desired Profile
            if(desiredProfiles != null && desiredProfiles.size() > 0){
            	int loadCount = 0;
            	for(Profile p: desiredProfiles){
                	try{
                		this.loadProfileWithoutCleanup(p);
                		loadCount++;
                		//add the profile to the recent profiles list
                		recentProfiles.addOrUpdateProfileInList(p);
                	}
                	catch(org.w3c.dom.ls.LSException e){
                    	this.reportErrorEvent(new ErrorEvent("The profile with the path \"" + p.getPath().toString() + "\" could not be found.",3));
                	}
                	catch(IOException e){
                    	this.reportErrorEvent(new ErrorEvent("The profile with the path \"" + p.getPath().toString() + "\" could not be red.",3));
                	}
                	catch(Exception e){
                    	this.reportErrorEvent(new ErrorEvent("An error occured while loading the profile with the path \"" + p.getPath().toString() + "\": " + e.toString() + " : " + e.getMessage(),4));
                	}
                }
            	//save the recent profiles list
            	try {
					saveRecentProfiles();
				} catch (IOException e) {
					this.reportErrorEvent(new ErrorEvent("Recent profiles could not be saved.",3));
				}
            	//Set the new profile name for multiple profiles
            	if(loadCount > 1){
            		this.currentProfile.setName("Multiple profiles");
            		//prevent the user from overwriting a file
            		this.currentProfile.setPath(new File(""));
            	}
            	//Otherwise the red profile name is used...
            	//send the profile changed event to update the name and path
            	profileChanged();
            }

            //Start the streams if -startall has been defined
            if(enableStartAll){
                //Fetch all senders
                Collection<? extends MulticastStream> senders = getSenders();
                startStreams(senders);
                //Fetch all receivers
                Collection<? extends MulticastStream> receivers = getReceiverGroups();
                startStreams(receivers);
            }
    }

    /*
     * This function will set the current profile and inform all observers
     */
    public void setCurrentProfile(Profile currentProfile) {
        if(currentProfile == null){
            throw new IllegalArgumentException();
        }
        //This will be our new profile
        this.currentProfile = currentProfile;
        //Inform observers
        profileChanged();
    }

    /*
     * This function will return the list of recently used profiles
     */
    public List<Profile> getRecentProfiles() {
        return recentProfiles.getProfileList();
    }


    /* (non-Javadoc)
     * @see com.spam.mctool.controller.StreamManager#addSender(java.util.HashMap)
     */
    public Sender addSender(HashMap<String, String> params) {
        return senderPool.create(params);
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.StreamManager#addReceiver(java.util.HashMap)
     */
    public ReceiverGroup addReceiver(HashMap<String, String> params) {
        return receiverPool.create(params);
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.StreamManager#removeStreams(java.util.Set)
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
     * @see com.spam.mctool.controller.StreamPool#startStreams(java.util.Set)
     */
    public void startStreams(Collection<? extends MulticastStream> streams) {
        //Fetch the set iterator
        for(MulticastStream m:streams){
            m.activate();
        }
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.StreamPool#stopStreams(java.util.Set)
     */
    public void stopStreams(Collection<? extends MulticastStream> streams) {
        for(MulticastStream m:streams){
            m.deactivate();
        }
    }

    /*
     * This function will save the sender/receiver settings to the specified profile path
     * in currentProfile.
     */
    public void saveProfile() throws Exception {
        //Register the xerces DOM-Implementation
        DOMImplementationRegistry registry = null;
        try {
            registry = DOMImplementationRegistry.newInstance();
        } catch (Exception e){
            System.out.println("DOM could not be serialized!");
            //TODO throw exception
        }

        //Load the xerces DOM implementation
        DOMImplementationLS impl =
            (DOMImplementationLS)registry.getDOMImplementation("LS");

        //Lets build a new factory for our XMLDocument
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = dbfac.newDocumentBuilder();
        } catch (ParserConfigurationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
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
        Text profileNameText = xmlDocument.createTextNode(currentProfile.getName());
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
        /*
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
                keyElement.appendChild(keyElement);
            }
        }
        */
        //Receivers section
        Element receiversElement = xmlDocument.createElement("receivers");
        rootElement.appendChild(receiversElement);

        //fetch all receivers
        java.util.Collection<ReceiverGroup> receivers = getReceiverGroups();
        Iterator<ReceiverGroup> itR = receivers.iterator();
        //iterate over the collection
        /*while(itR.hasNext()){
            ReceiverGroup curReceiver = itR.next();
            //Create receiver element
            Element receiverElement = xmlDocument.createElement("receiver");
            receiversElement.appendChild(receiverElement);
            //Fetch the receiver configuration MAP
            ;Map<String, String> map = curReceiver.getConfiguration();
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
                keyElement.appendChild(keyElement);
            }
        }*/

        //get the desired profile path
        File profilePath = this.currentProfile.getPath();

        //Our LSSerializer will create the XML output
        LSSerializer writer = impl.createLSSerializer();
        //Beautiful formatted output
        writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
        //Try to write it to the file
        try{
            writer.writeToURI(xmlDocument, profilePath.toString());
        }
        catch(LSException e){
            System.out.println("The data could not be serialized to XML.");
            throw new Exception("The data could not be serialized to XML.");
        }
        catch(Exception e){
            System.out.println("The file could not be written.");
            throw new Exception("The file could not be written.");

        }
        //TODO Remove debug ouput
        //String xmlString = writer.writeToString(xmlDocument);
        //System.out.println(xmlString);
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.ProfilePool#loadProfile()
     */
    public void loadProfile() throws org.w3c.dom.ls.LSException,IOException,Exception{
        //First of all delete all senders and receivers
        removeStreams(getReceiverGroups());
        removeStreams(getSenders());

        //Load the profile
        loadProfileWithoutCleanup(this.currentProfile);

        //Add it to the list of recent profiles
        recentProfiles.addOrUpdateProfileInList(this.currentProfile);

        //Signalize, that the profile has changed
        profileChanged();
    }

    public void loadProfileWithoutCleanup(Profile p) throws org.w3c.dom.ls.LSException,IOException,Exception{
    	if(p == null){
    		throw new IllegalArgumentException();
    	}

        DOMImplementationRegistry registry=null;
        try {
            registry = DOMImplementationRegistry.newInstance();
        } catch (Exception e){
        	throw new Exception("DOM could not be initialized.");
        }

        DOMImplementationLS impl =
            (DOMImplementationLS)registry.getDOMImplementation("LS");

        LSParser builder = impl.createLSParser(
            DOMImplementationLS.MODE_SYNCHRONOUS, null);

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
                //set the name but don't send a profilechanged event
                this.currentProfile.setName(name);
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
                //fetch all nodes
                NodeList senderData = curSender.getChildNodes();
                //create a map
                Map<String,String> map = new HashMap<String, String>();
                for(int j = 0;j<senderData.getLength();++j){
                    Node curData = senderData.item(j);
                    //put the pair to the map
                    map.put(curData.getNodeName(), curData.getTextContent());
                }
                //Try to add the sender
                try{
                    addSender(map);
                }
                catch(Exception e){
                    this.reportErrorEvent(new ErrorEvent("A sender could not be added: " + e.getMessage()+ " : " +e.toString(), 3));
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
                //fetch all nodes
                NodeList receiverData = curReceiver.getChildNodes();
                //create a map
                Map<String,String> map = new HashMap<String, String>();
                for(int j = 0;j<receiverData.getLength();++j){
                    Node curData = receiverData.item(j);
                    //put the pair to the map
                    map.put(curData.getNodeName(), curData.getTextContent());
                }
                //Try to add the sender
                try{
                    addReceiverGroup(map);
                }
                catch(Exception e){
                    this.reportErrorEvent(new ErrorEvent("A receiver could not be added: " + e.getMessage()+ " : " +e.toString(), 3));
                }
            }
        }
        //at this point the profile has been loaded.
        //Update the current path
        this.currentProfile.setPath(p.getPath());
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.ProfilePool#addProfileChangeListener(com.spam.mctool.controller.ProfileChangeListener)
     */
    public void addProfileChangeListener(ProfileChangeListener listener) {
        this.profileChangeObservers.add(listener);
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.ProfilePool#removeProfileChangeListener(com.spam.mctool.controller.ProfileChangeListener)
     */
    public void removeProfileChangeListener(ProfileChangeListener listener) {
        this.profileChangeObservers.remove(listener);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        controller = new Controller();
        controller.init(args);

        //controller.setCurrentProfile(new Profile("Test",new File("Test.xml")));
        //controller.storeCurrentProfile();
        //controller.loadProfile();
    }

    public Sender addSender(Map<String, String> params) {
        return this.senderPool.create(params);
    }

    public ReceiverGroup addReceiverGroup(Map<String, String> params) {
        return receiverPool.create(params);
    }

    public Collection<Sender> getSenders() {
        return senderPool.getSenders();
    }

    public Collection<ReceiverGroup> getReceiverGroups() {
        return receiverPool.getReceiverGroups();
    }

    public void addSenderAddedOrRemovedListener(SenderAddedOrRemovedListener l) {
        senderPool.addSenderAddedOrRemovedListener(l);
    }

    public void removeSenderAddedOrRemovedListener(
            SenderAddedOrRemovedListener l) {
        senderPool.removeSenderAddedOrRemovedListener(l);
    }

    public void addReceiverAddedOrRemovedListener(
            ReceiverAddedOrRemovedListener l) {
        receiverPool.addReceiverAddedOrRemovedListener(l);
    }

    public void removeReceiverAddedOrRemovedListener(
            ReceiverAddedOrRemovedListener l) {
        receiverPool.removeReceiverAddedOrRemovedListener(l);
    }

    public void storeCurrentProfile() {
        try {
            saveProfile();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //if successfully saved, add it to the list of recent profiles
        recentProfiles.addOrUpdateProfileInList(this.currentProfile);
        //save the recent profile list
        try {
            saveRecentProfiles();
        } catch (IOException e) {
            this.reportErrorEvent(new ErrorEvent("The recent profiles list could not ba saved: " + e.getMessage(),3));
        }
    }

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
        else if(errorLevel > 5){
            errorLevel = 5;
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
            System.out.println(dateFormat.format(date) + ": Level " + errorLevel + " :" + e.getErrorMessage());

        }
        //iterate over all listener
        for(ErrorEventListener l: newErrorEventObservers){
            //The listener will only be called if the event error level is higher or equal to the desired level
            if(this.newErrorEventObserversErrorLevel.get(l) >= errorLevel){
                l.newErrorEvent(e);
            }
        }

    }
}
