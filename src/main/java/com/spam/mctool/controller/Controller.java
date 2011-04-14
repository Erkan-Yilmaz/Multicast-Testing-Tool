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
public class Controller implements ProfileManager, StreamManager {

    private static Controller controller;
    private Profile currentProfile;
    private List<Profile> recentProfiles;
    private List<ProfileChangeListener> profileChangeObservers;
    private SenderManager senderPool;
    private ReceiverManager receiverPool;
    private List<MctoolView> viewers;

    /* This function either inserts the profile directly to the top
     * of the list or searches for an existing entry(path!), deletes that entry
     * and inserts the new profile at te top. Max 10 profiles are stored.
     */
    private void addOrUpdateProfileInList(Profile profile){
        //test for null value
        if(profile == null){
            throw new IllegalArgumentException();
        }
        //Search for existing entries with equal path and delete them
        Iterator<Profile> it = this.recentProfiles.iterator();
        while(it.hasNext()){
            Profile compareObject = it.next();
            //Delete the entry
            if(profile.equalPath(compareObject)){
                this.recentProfiles.remove(compareObject);
            }
        }
        //Add the new profile to the top
        this.recentProfiles.add(0, profile);
        //Test if there are more then 10 elements in the list
        if(this.recentProfiles.size() > 10){
            //delete the 11. entry until the size equals 10
            while(this.recentProfiles.size() > 10){
                this.recentProfiles.remove(10);
            }
        }
    }

    /*
     * This function tries to save the profile list to the file named "RecentProfiles.xml"
     */
    private void saveRecentProfiles() throws IOException{
        //create the xstream object
        XStream xstream = new XStream();
        //convert the recent profiles to xml
        String xml = xstream.toXML(recentProfiles);
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
        //create the xstream object
        XStream xstream = new XStream();
        //create a new file
        File recentProfilesPath = new File("RecentProfiles.xml");
        //create a file input stream
        FileInputStream fis = new FileInputStream(recentProfilesPath);
        //create a data input stream
        DataInputStream dis = new DataInputStream(fis);
        //read the xml data
        String xml = dis.readUTF();
        System.out.println(xml);
        //create the object from xstream
        this.recentProfiles = (ArrayList<Profile>)xstream.fromXML(xml);
    }


    private Controller(){
        this.currentProfile = new Profile();
        this.recentProfiles = new ArrayList<Profile>();
        this.profileChangeObservers = new ArrayList<ProfileChangeListener>();
        //Init the Sender and Receiver modules
        this.senderPool = new SenderPool();
        this.receiverPool = new ReceiverPool();
        //Create the vies
        viewers = new ArrayList<MctoolView>();
    }

    private void profileChanged(){
        //Get the iterator for the observer list
        ListIterator<ProfileChangeListener> it = profileChangeObservers.listIterator();
        while(it.hasNext()){
            ProfileChangeListener observer = it.next();
            //Inform the observer
            observer.profileChanged(new ProfileChangeEvent());
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
                System.out.println("The recent profiles list could not be read. The file 'RecentProfiles.xml' does not exist.");
            } catch (IOException e) {
                System.out.println("The recent profiles list could not be read. An IOException occured");
            }
            //Gui enabled by default
            boolean enableGui = true;
            //Cli disabled by default
            boolean enableCli = false;
            //Start all loaded senders and receivers later?
            boolean enableStartAll = false;
            //The profile to be loaded
            File desiredProfile = null;
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
                //load profile?
                else if(args[i].compareToIgnoreCase("-profile") == 0){
                    //read the next argument if available
                    if((i+1) >= args.length){
                        //TODO: Error Message, no profile is defined
                    }
                    else{
                        if(args[i].charAt(0) == '-'){
                            //TODO this really can't be a name or path
                        }
                        else if(args[i].contains(":/\\")){
                            //This is a path, load it
                            desiredProfile = new File("args[i]");
                            //TODO error
                            ++i;
                        }
                        else{
                            //This could be a name of a recently used profile
                            //Search for it
                            Iterator<Profile> it = this.recentProfiles.iterator();
                            while(it.hasNext()){
                                it.next();
                                String name = ((Profile)it).getName();
                                //found the name?
                                if( name.compareTo(args[i+1]) != 0){
                                    desiredProfile = ((Profile)it).getPath();
                                    break;
                                }
                            }
                            ++i;
                        }
                    }
                }
                //start all receivers and sender
                else if(args[i].compareToIgnoreCase("-startall") == 0){
                    enableStartAll = true;
                }
                else{
                    //TODO Keyword not valid
                }

            }
            //Add views here

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
            while(it.hasNext()){
                    MctoolView curView = it.next();
                    curView.init(this);
            }

            //TODO Load desired Profile

            //TODO Start the streams if -startall has been defined
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
        return recentProfiles;
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
    public void removeStreams(Set<MulticastStream> streams) {
        //Fetch the set iterator
        Iterator<MulticastStream> it = streams.iterator();
        while(it.hasNext()){
            MulticastStream curStream = it.next();
            if(curStream instanceof Sender){
                senderPool.remove((Sender)curStream);
            }
            else if(curStream instanceof ReceiverGroup){
                receiverPool.remove((ReceiverGroup)curStream);
            }
            else{
                throw new IllegalArgumentException();
            }
        }
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.StreamPool#startStreams(java.util.Set)
     */
    public void startStreams(Set<MulticastStream> streams) {
        //Fetch the set iterator
        Iterator<MulticastStream> it = streams.iterator();
        //Activate all streams
        while(it.hasNext()){
            it.next().activate();
        }
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.StreamPool#stopStreams(java.util.Set)
     */
    public void stopStreams(Set<MulticastStream> streams) {
        //Fetch the set iterator
        Iterator<MulticastStream> it = streams.iterator();
        //Dectivate all streams
        while(it.hasNext()){
            it.next().deactivate();
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
        String xmlString = writer.writeToString(xmlDocument);
        System.out.println(xmlString);
    }

    /* (non-Javadoc)
     * @see com.spam.mctool.controller.ProfilePool#loadProfile()
     */
    public void loadProfile() {
        DOMImplementationRegistry registry=null;
        try {
            registry = DOMImplementationRegistry.newInstance();
        } catch (ClassCastException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        DOMImplementationLS impl =
            (DOMImplementationLS)registry.getDOMImplementation("LS");

        LSParser builder = impl.createLSParser(
            DOMImplementationLS.MODE_SYNCHRONOUS, null);

        //Fetch the profile path
        File xmlPath = currentProfile.getPath();
        System.out.println(xmlPath.toString());
        //Parse the file
        Document xmlDocument = builder.parseURI(xmlPath.toString());
        //Find the profile section
        Node profile = xmlDocument.getElementsByTagName("profile").item(0);
        if(profile.getNodeType() == Node.ELEMENT_NODE){
            System.out.println((profile.getChildNodes().item(0).getTextContent()));
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

        //Test
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
        this.addOrUpdateProfileInList(this.currentProfile);
        //save the recent profile list
        try {
            saveRecentProfiles();
        } catch (IOException e) {
            System.out.println("The recent profile list could not be saved, an IOException was thrown.");
        }
    }

    private static final String MAZE_ELEMENT = "maze";

}
