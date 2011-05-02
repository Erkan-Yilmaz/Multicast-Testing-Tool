package com.spam.mctool.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.xstream.XStream;

public class RecentProfiles {
    /**
     * The list storing recent profiles.
     */
    private List<Profile> profileList;

    /**
     * The default constructor. Will initialize an empty list for the recent profiles.
     */
    public RecentProfiles(){
        this.profileList = new ArrayList<Profile>();
    }

    /**
     * Get the recent profiles list.
     * @return The profile list.
     */
    public List<Profile> getProfileList() {
        return profileList;
    }

    /**
     * Set the profile list. This function should not be used.
     * @param profileList The profile list to be set.
     */
    public void setProfileList(List<Profile> profileList) {
        this.profileList = profileList;
    }

    /* This function either inserts the profile directly to the top
     * of the list or searches for an existing entry(path!), deletes that entry
     * and inserts the new profile at the top. This will deny the storage of equal profiles
     * in the list. Max 10 profiles are stored.
     * @param profile The profile to be updated/added to the list.
     */
    public void addOrUpdateProfileInList(Profile profile){
        //test for null value
        if(profile == null){
            throw new IllegalArgumentException();
        }
        //make a local copy of the list
		ArrayList<Profile> profileListCopy = new ArrayList<Profile>(profileList);
		//Check in each loaded recent profile
        profileList.remove(profile);
        //Add the new profile to the top
        this.profileList.add(0, profile);
        //Test if there are more then 10 elements in the list
        if(this.profileList.size() > 10){
            //delete the 11. entry until the size equals 10
            while(this.profileList.size() > 10){
                this.profileList.remove(10);
            }
        }
    }

    /**
     * This method will convert the recent profiles list to xml data using xstream.
     * @return XML data of the recent profiles list as string.
     */
    public String toXML(){
        //create the xstream object
        XStream xstream = new XStream();
        //convert the recent profiles to xml
        return xstream.toXML(profileList);
    }

    /**
     * This method will set the recent profiles list from xml data extracted previously using toXML() using xstream.
     * @param xml The xml data to be used to deserialize the list.
     */
    public void fromXML(String xml){
        if(xml==null){
            throw new IllegalArgumentException();
        }
        //create the xstream object
        XStream xstream = new XStream();
        this.profileList = (ArrayList<Profile>)xstream.fromXML(xml);
        removeDeletedProfiles();
    }

    /**
     * This function will scan through the recent profile list, searching for a recently used profile with the given name.
     * @param name The name to be searched for in the list.
     * @return null if no profile has been found. Otherwise the first profile matching the given name.
     */
    public Profile findProfileByName(String name){
        //if the string is null, we will find no profile...
        if(name == null){
            return null;
        }
        Profile foundProfile = null;
        //Fetch the iterator
        for(Profile p: profileList){
            String profileName = p.getName();
            //found the name?
            if( name.compareTo(profileName) == 0){
                foundProfile = p;
                break;
            }
        }
        return foundProfile;
    }

	/**
	 * This function will scan through the recent profile list, removing all profiles which can't be found.
	 */
	private void removeDeletedProfiles(){
		if(profileList != null){
			//make a local copy of the list
			ArrayList<Profile> profileListCopy = new ArrayList<Profile>(profileList);
			//Check in each loaded recent profile
			for(Profile p:profileListCopy){
				//Remove the file if it doesn't exist
				if(!p.getPath().exists()){
					profileList.remove(p);
				}
			}
		}
	}
}