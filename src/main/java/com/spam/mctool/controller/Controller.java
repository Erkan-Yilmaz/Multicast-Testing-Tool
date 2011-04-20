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
                this.reportErrorEvent(new ErrorEvent(1,"Controller.failedLoadingRecentProfiles.text",""));
            } catch (IOException e) {
                this.reportErrorEvent(new ErrorEvent(1,"Controller.failedLoadingRecentProfiles2.text",""));
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
                    	this.reportErrorEvent(new ErrorEvent(5,"Controller.missingArgumentProfileName.text",""));
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
                        	this.reportErrorEvent(new ErrorEvent(2,"Controller.nameNotFoundInRecentProfiles.text", args[i]));
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
                    	this.reportErrorEvent(new ErrorEvent(5,"Controller.missingArgumentProfilePath.text", ""));
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
                	this.reportErrorEvent(new ErrorEvent(5, "Controller.unknownArgument.text", args[i]));
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
            	Profile loadedProfile = null;
            	for(Profile p: desiredProfiles){
                	try{
                		this.loadProfileWithoutCleanup(p,"default");
                		loadCount++;
                		//add the profile to the recent profiles list
                		recentProfiles.addOrUpdateProfileInList(p);
                		loadedProfile = p;
                	}
                	catch(org.w3c.dom.ls.LSException e){
                    	this.reportErrorEvent(new ErrorEvent(3,"Controller.profileLoadingError.text",p.getPath().toString() + ": " + e.getLocalizedMessage()));
                	}
                	catch(IOException e){
                    	this.reportErrorEvent(new ErrorEvent(3,"Controller.profileLoadingError2.text", p.getPath().toString()));
                	}
                	catch(Exception e){
                    	this.reportErrorEvent(new ErrorEvent(4,"Controller.profileLoadingError3.text",p.getPath().toString() + ": " + e.getLocalizedMessage()));
                	}
                }
            	//save the recent profiles list
            	try {
					saveRecentProfiles();
				} catch (IOException e) {
					this.reportErrorEvent(new ErrorEvent(3,"Controller.failedSavingRecentProfiles.text",e.getLocalizedMessage()));
				}
            	//Set the new profile name for multiple profiles
            	if(loadCount > 1){
            		this.setCurrentProfile(new Profile("Multiple profiles loaded.",new File("")));
            	}
            	//Only one profile loaded? make it the active one
            	else if(loadedProfile!=null){
            		this.setCurrentProfile(loadedProfile);
            	}
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
    public void saveProfileToFile(Profile p) throws Exception {
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
    		this.loadProfileWithoutCleanup(p,"default");
    	}
    	catch(org.w3c.dom.ls.LSException e){
        	this.reportErrorEvent(new ErrorEvent(3,"Controller.profileLoadingError.text",p.getPath().toString() + ": " + e.getLocalizedMessage()));
    	}
    	catch(IOException e){
        	this.reportErrorEvent(new ErrorEvent(3,"Controller.profileLoadingError2.text", p.getPath().toString()));
    	}
    	catch(Exception e){
        	this.reportErrorEvent(new ErrorEvent(4,"Controller.profileLoadingError3.text",p.getPath().toString() + ": " + e.getLocalizedMessage()));
    	}

        //Add it to the list of recent profiles
        recentProfiles.addOrUpdateProfileInList(p);
        
        //Make it the new profile and signalize it to the observers
        this.setCurrentProfile(p);
    }

    public void loadProfileWithoutCleanup(Profile p,String startModus) throws org.w3c.dom.ls.LSException,IOException,Exception{
    	if(p == null){
    		throw new IllegalArgumentException();
    	}
    	if(startModus == null){
    		startModus = "default";
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
                    this.reportErrorEvent(new ErrorEvent(3,"Controller.failedAddingSender", e.getLocalizedMessage()));
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
                    this.reportErrorEvent(new ErrorEvent(3,"Controller.failedAdddingReceiverGroup.text", e.getLocalizedMessage()));
                }
            }
        }
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

    public void saveProfile(Profile p) {
    	if(p == null){
    		throw new IllegalArgumentException();
    	}
    	try {
            saveProfileToFile(p);
        } catch (Exception e) {
            this.reportErrorEvent(new ErrorEvent(3,"Controller.profileSavingError.text",p.getPath() + ": " + e.getLocalizedMessage()));
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
            this.reportErrorEvent(new ErrorEvent(3,"Controller.failedSavingRecentProfiles.text",e.getLocalizedMessage()));
        }
    }
    
    public void saveCurrentProfile(){
    	saveProfile(this.currentProfile);
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
            System.out.println(dateFormat.format(date) + ": Level " + errorLevel + " :" + e.getCompleteMessage());

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
