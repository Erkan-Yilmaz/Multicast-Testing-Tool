/**
 * 
 */
package com.spam.mctool.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import com.spam.mctool.intermediates.ProfileChangeEvent;
import com.spam.mctool.model.MulticastStream;
import com.spam.mctool.model.ReceiverAddedOrRemovedListener;
import com.spam.mctool.model.ReceiverGroup;
import com.spam.mctool.model.ReceiverPool;
import com.spam.mctool.model.Sender;
import com.spam.mctool.model.SenderAddedOrRemovedListener;
import com.spam.mctool.model.SenderPool;
import com.spam.mctool.view.CommandLineView;
import com.spam.mctool.view.GraphicalView;
import com.spam.mctool.view.MctoolView;

/**
 * @author davidhildenbrand
 *
 */
public class Controller implements ProfileManager, StreamManager {
	
	private static Controller controller;
	private Profile currentProfile;
	private List<Profile> recentProfiles;
	private List<ProfileChangeListener> profileChangeObservers;
	private SenderPool senderManager;
	private ReceiverPool receiverManager;
	private List<MctoolView> viewers;
	
	private Controller(){
		this.currentProfile = new Profile();
		this.recentProfiles = new ArrayList<Profile>();
		this.profileChangeObservers = new ArrayList<ProfileChangeListener>();
		//Init the Sender and Receiver modules
		this.senderManager = new SenderPool();
		this.receiverManager = new ReceiverPool();
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
			//TODO load recent profiles
			//Gui enabled by default
			boolean enableGui = true;
			//Cli disabled by default
			boolean enableCli = false;
			//Start all loaded senders and receivers later?
			boolean enableStartAll = false;
			//The profile to be loaded
			File desiredProfile;
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
	}

	public void setCurrentProfile(Profile currentProfile) {
		//Put it right into the recentProfiles list
		this.recentProfiles.add(currentProfile);
		this.currentProfile = currentProfile;
		//Inform observers
		profileChanged();
	}

	public List<Profile> getRecentProfiles() {
		return recentProfiles;
	}
	/*
	public void setRecentProfiles(ArrayList<Profile> recentProfiles) {
		this.recentProfiles = recentProfiles;
	}
	*/

	/* (non-Javadoc)
	 * @see com.spam.mctool.controller.StreamManager#addSender(java.util.HashMap)
	 */
	public Sender addSender(HashMap<String, String> params) {
		return senderManager.create(params);
	}

	/* (non-Javadoc)
	 * @see com.spam.mctool.controller.StreamManager#addReceiver(java.util.HashMap)
	 */
	public ReceiverGroup addReceiver(HashMap<String, String> params) {
		return receiverManager.create(params);
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
				senderManager.remove((Sender)curStream);
			}
			else if(curStream instanceof ReceiverGroup){
				receiverManager.remove((ReceiverGroup)curStream);
			}
			else{
				throw new IllegalArgumentException();
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.spam.mctool.controller.StreamManager#startStreams(java.util.Set)
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
	 * @see com.spam.mctool.controller.StreamManager#stopStreams(java.util.Set)
	 */
	public void stopStreams(Set<MulticastStream> streams) {
		//Fetch the set iterator
		Iterator<MulticastStream> it = streams.iterator();
		//Dectivate all streams
		while(it.hasNext()){
			it.next().deactivate();
		}
	}

	/* (non-Javadoc)
	 * @see com.spam.mctool.controller.ProfileManager#saveProfile()
	 */
	public void saveProfile() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.spam.mctool.controller.ProfileManager#loadProfile()
	 */
	public void loadProfile() {
		// TODO Auto-generated method stub

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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		controller = new Controller();
		controller.init(args);
	}

	public Sender addSender(Map<String, String> params) {
		return this.senderManager.create(params);
	}

	public ReceiverGroup addReceiverGroup(Map<String, String> params) {
		return receiverManager.create(params);
	}

	public Collection<Sender> getSenders() {
		return senderManager.getSenders();
	}

	public Collection<ReceiverGroup> getReceiverGroups() {
		return receiverManager.getReceiverGroups();
	}

	public void addSenderAddedOrRemovedListener(SenderAddedOrRemovedListener l) {
		senderManager.addSenderAddedOrRemovedListener(l);
	}

	public void removeSenderAddedOrRemovedListener(
			SenderAddedOrRemovedListener l) {
		senderManager.removeSenderAddedOrRemovedListener(l);
	}

	public void addReceiverAddedOrRemovedListener(
			ReceiverAddedOrRemovedListener l) {
		receiverManager.addReceiverAddedOrRemovedListener(l);
	}

	public void removeReceiverAddedOrRemovedListener(
			ReceiverAddedOrRemovedListener l) {
		receiverManager.removeReceiverAddedOrRemovedListener(l);
	}

	public void storeCurrentProfile() {
		// TODO Do the serialization	
	}
	

}
