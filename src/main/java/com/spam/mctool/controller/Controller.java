/**
 * 
 */
package com.spam.mctool.controller;

import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Set;

import javax.swing.text.html.HTMLDocument.Iterator;

import com.spam.mctool.intermediates.ProfileChangeEvent;
import com.spam.mctool.model.MulticastStream;
import com.spam.mctool.model.Receiver;
import com.spam.mctool.model.Sender;
import com.spam.mctool.model.SenderManager;

/**
 * @author davidhildenbrand
 *
 */
public class Controller implements ProfileManager, StreamManager {
	
	private static Controller controller;
	private Profile currentProfile;
	private ArrayList<Profile> recentProfiles;
	private ArrayList<ProfileChangeListener> profileChangeObservers;
	private Sender sender;
	private Receiver receiver;
	
	private Controller(){
		this.currentProfile = new Profile();
		this.recentProfiles = new ArrayList<Profile>();
		this.profileChangeObservers = new ArrayList<ProfileChangeListener>();
		//Init the Sender and Receiver modules
		this.sender = new Sender();
		this.receiver = new Receiver();

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

	public void setCurrentProfile(Profile currentProfile) {
		//Put it right into the recentProfiles list
		this.recentProfiles.add(currentProfile);
		this.currentProfile = currentProfile;
		//Inform observers
		profileChanged();
	}

	public ArrayList<Profile> getRecentProfiles() {
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
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.spam.mctool.controller.StreamManager#addReceiver(java.util.HashMap)
	 */
	public Receiver addReceiver(HashMap<String, String> params) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.spam.mctool.controller.StreamManager#removeStreams(java.util.Set)
	 */
	public void removeStreams(Set<MulticastStream> streams) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.spam.mctool.controller.StreamManager#startStreams(java.util.Set)
	 */
	public void startStreams(Set<MulticastStream> streams) {
		MulticastStream senders = sender.
		Iterator it = (Iterator)streams.iterator();
		while(it.next()){
			
		}
	}

	/* (non-Javadoc)
	 * @see com.spam.mctool.controller.StreamManager#stopStreams(java.util.Set)
	 */
	public void stopStreams(Set<MulticastStream> streams) {
		// TODO Auto-generated method stub

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

	/* (non-Javadoc)
	 * @see com.spam.mctool.controller.ProfileManager#setDefaultLanguage(java.lang.Object)
	 */
	public void setDefaultLanguage(Object l) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.spam.mctool.controller.ProfileManager#getDefaultLanguage()
	 */
	public Object getDefaultLanguage() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		controller = new Controller();
	}
	

}
