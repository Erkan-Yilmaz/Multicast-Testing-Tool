package com.spam.mctool.controller;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class RecentProfilesTest {

	@Test
	public void testGetProfileList() {
		//Create a new RecentProfiles object
		RecentProfiles recentProfiles = new RecentProfiles();
		//fetch the profiles list
		List<Profile> profileList = recentProfiles.getProfileList();
		//must not be null
		assertTrue(profileList != null);
		//must be an empty List
		assertEquals(profileList.size(), 0);
		//add a new Element
		profileList.add(new Profile());
		//Fetch the list once again
		profileList = recentProfiles.getProfileList();
		//must not be null
		assertTrue(profileList != null);
		//must have one Element in the list
		assertEquals(profileList.size(), 1);
	}

	@Test
	public void testSetProfileList() {
		//Create a new RecentProfiles object
		RecentProfiles recentProfiles = new RecentProfiles();
		//try to set the list to null
		try{
			recentProfiles.setProfileList(null);
			fail("Exception not thrown");
		}
		catch (Exception e) {
			//must be an IllegalArgumentEception
			assertTrue(e instanceof IllegalArgumentException);
		}
		//create a new empty list
		List<Profile> profileList = new ArrayList<Profile>();
		//try to set the list
		recentProfiles.setProfileList(profileList);
		//asert that it has been set
		assertEquals(recentProfiles.getProfileList(), profileList);
		//Create a new list
		profileList = new ArrayList<Profile>();
		//add one profile
		profileList.add(new Profile());
		//try to set the list
		recentProfiles.setProfileList(profileList);
		//asert that it has been set
		assertEquals(recentProfiles.getProfileList(), profileList);
	}

	@Test
	public void testAddOrUpdateProfileInList() {
		//Create a new RecentProfiles object
		RecentProfiles recentProfiles = new RecentProfiles();
		//Create a few profiles
		Profile profile1 = new Profile("Test1",new File("Pfad 1"));
		Profile profile2 = new Profile("Test2",new File("Pfad 2"));
		//Profile1 and 3 share the same path
		Profile profile3 = new Profile("Test3",new File("Pfad 1"));

		//Add profile1
		recentProfiles.addOrUpdateProfileInList(profile1);
		//create a temporary list
		List<Profile> tempList = new ArrayList<Profile>();
		//Add profile1 to the tempList
		tempList.add(profile1);
		//Both lists should be the same
		assertEquals(recentProfiles.getProfileList(), tempList);
		//Add profile 2
		recentProfiles.addOrUpdateProfileInList(profile2);
		//Add it to the top of tempList
		tempList.add(0, profile2);
		//Both lists should be the same
		assertEquals(recentProfiles.getProfileList(), tempList);
		//add profile 3, which should replace profile1 and move it to the top
		//-> Equal path!
		//Add profile 3
		recentProfiles.addOrUpdateProfileInList(profile3);
		//Add it once again
		recentProfiles.addOrUpdateProfileInList(profile3);
		//create a new tempList
		tempList = new ArrayList<Profile>();
		//add first profile3, than profile2
		tempList.add(profile3);
		tempList.add(profile2);
		//Both lists should be the same
		assertEquals(recentProfiles.getProfileList(), tempList);
	}

	@Test
	public void testToXML() {
		//Create a new RecentProfiles object
		RecentProfiles recentProfiles = new RecentProfiles();
		//convert it to xml
		assertNotNull(recentProfiles.toXML());
		assertEquals(recentProfiles.toXML(),"<list/>");
		//Create two profiles
		Profile profile1 = new Profile("Test1",new File("Pfad 1"));
		Profile profile2 = new Profile("Test2",new File("Pfad 2"));
		//Add them
		recentProfiles.addOrUpdateProfileInList(profile1);
		recentProfiles.addOrUpdateProfileInList(profile2);
		//convert it to xml
		assertNotNull(recentProfiles.toXML());
		assertEquals(recentProfiles.toXML(),"<list>\n  <com.spam.mctool.controller.Profile>\n    <name>Test2</name>\n    <path>Pfad 2</path>\n  </com.spam.mctool.controller.Profile>\n  <com.spam.mctool.controller.Profile>\n    <name>Test1</name>\n    <path>Pfad 1</path>\n  </com.spam.mctool.controller.Profile>\n</list>");
	}

	@Test
	public void testFromXML() {
		//Create a new RecentProfiles object
		RecentProfiles recentProfiles = new RecentProfiles();
		//fetch the list
		List<Profile> tempList = recentProfiles.getProfileList();
		//Add a profile
		tempList.add(new Profile("Test1",new File("Pfad 1")));
		//load an empty list from xml
		recentProfiles.fromXML("<list/>");
		//the lists should be different
		assertNotSame(tempList, recentProfiles.getProfileList());
		//the loaded list should have the size 0
		assertEquals(recentProfiles.getProfileList().size(),0);
		//load another list from xml
		//NOTE: This refereneced PROFILE MUST EXIST!!!
		//Create the empty profile file
		File testprofile = new File("testprofile");
		try {
			testprofile.createNewFile();
		} catch (IOException e1) {
			fail("testprofile could not be created");
		}
		recentProfiles.fromXML("<list><com.spam.mctool.controller.Profile><name>Test</name><path>testprofile</path></com.spam.mctool.controller.Profile></list>");
		//create the tempList to compare with
		tempList = new ArrayList<Profile>();
		tempList.add(new Profile("Test",new File("testprofile")));
		//load a list with a profile WHICH DOES NOT EXIST -> Deleted after checking the path!
		recentProfiles.fromXML("<list><com.spam.mctool.controller.Profile><name>Test2</name><path>nonsense</path></com.spam.mctool.controller.Profile></list>");
		//The list will be empty as the profile file does not exist!
		tempList = new ArrayList<Profile>();
		assertEquals(tempList, recentProfiles.getProfileList());
		//try to load null
		try{
			recentProfiles.fromXML(null);
			fail("Exception not thrown");
		}
		catch(Exception e){
			assertTrue(e instanceof IllegalArgumentException);
		}
		//try to load nonsense
		try{
			recentProfiles.fromXML("holla die waldfee");
			fail("Exception not thrown");
		}
		catch(Exception e){
			//Parsing exception
			assertTrue(e instanceof com.thoughtworks.xstream.io.StreamException);
		}
	}

	@Test
	public void testFindProfileByName() {
		//Create a new RecentProfiles object
		RecentProfiles recentProfiles = new RecentProfiles();
		//Create some profiles
		Profile profile1 = new Profile("Test1",new File("Pfad 1"));
		Profile profile2 = new Profile("Test2",new File("Pfad 2"));
		Profile profile3 = new Profile("Test3",new File("Pfad 3"));
		//Add the profiles
		recentProfiles.addOrUpdateProfileInList(profile1);
		recentProfiles.addOrUpdateProfileInList(profile2);
		recentProfiles.addOrUpdateProfileInList(profile3);
		//Look up null which will return null
		Profile lookup = recentProfiles.findProfileByName(null);
		assertNull(lookup);
		//Look up profile1
		lookup = recentProfiles.findProfileByName("Test1");
		assertEquals(lookup, profile1);
		//Look up profile2
		lookup = recentProfiles.findProfileByName("Test2");
		assertEquals(lookup, profile2);
		//Look up profile3
		lookup = recentProfiles.findProfileByName("Test3");
		assertEquals(lookup, profile3);
		//Look up nonsense which will return null
		lookup = recentProfiles.findProfileByName("Banane");
		assertEquals(lookup, null);
	}

}
