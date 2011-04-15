package com.spam.mctool.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.xstream.XStream;

public class RecentProfiles {
    private List<Profile> profileList;
    
    public RecentProfiles(){
        this.profileList = new ArrayList<Profile>();
    }

    public List<Profile> getProfileList() {
        return profileList;
    }

    public void setProfileList(List<Profile> profileList) {
        this.profileList = profileList;
    }
    
    /* This function either inserts the profile directly to the top
     * of the list or searches for an existing entry(path!), deletes that entry
     * and inserts the new profile at te top. Max 10 profiles are stored.
     */
    public void addOrUpdateProfileInList(Profile profile){
        //test for null value
        if(profile == null){
            throw new IllegalArgumentException();
        }
        //Search for existing entries with equal path and delete them
        for(Profile p:profileList){
            if(profile.equalPath(p)){
                this.profileList.remove(p);
            }
        }
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
    
    public String toXML(){
        //create the xstream object
        XStream xstream = new XStream();
        //convert the recent profiles to xml
        return xstream.toXML(profileList);
    }
    
    public void fromXML(String xml){
        if(xml==null){
            throw new IllegalArgumentException();
        }
        //create the xstream object
        XStream xstream = new XStream();
        this.profileList = (ArrayList<Profile>)xstream.fromXML(xml);
    }
    
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
            if( name.compareTo(profileName) != 0){
                foundProfile = p;
                break;
            }
        }
        return foundProfile;
    }
}
