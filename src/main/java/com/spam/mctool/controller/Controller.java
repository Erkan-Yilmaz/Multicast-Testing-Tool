/**
 * 
 */
package com.spam.mctool.controller;

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
import com.spam.mctool.model.Receiver;
import com.spam.mctool.model.ReceiverAddedOrRemovedListener;
import com.spam.mctool.model.ReceiverManager;
import com.spam.mctool.model.Sender;
import com.spam.mctool.model.SenderAddedOrRemovedListener;
import com.spam.mctool.model.SenderManager;
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
	private SenderManager senderManager;
	private ReceiverManager receiverManager;
	private List<MctoolView> viewers;
	
	private Controller(){
		this.currentProfile = new Profile();
		this.recentProfiles = new ArrayList<Profile>();
		this.profileChangeObservers = new ArrayList<ProfileChangeListener>();
		//Init the Sender and Receiver modules
		//this.senderManager = new SenderPool();
		//this.receiverManager = new ReceiverPool();
		viewers = new ArrayList<MctoolView>();
		viewers.add(new GraphicalView()); // Added by TST. uncomment to
                                                    // display the gui upon
                                                    // instantiation
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
                //Add views here

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
	public Receiver addReceiver(HashMap<String, String> params) {
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
			else if(curStream instanceof Receiver){
				receiverManager.remove((Receiver)curStream);
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
		// TODO Auto-generated method stub
		return null;
	}

	public Receiver addReceiver(Map<String, String> params) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<Sender> getSenders() {
		return senderManager.getSenders();
	}

	public Collection<Receiver> getReceivers() {
		return receiverManager.getReceiver();
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
