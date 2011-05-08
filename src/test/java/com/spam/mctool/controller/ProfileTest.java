package com.spam.mctool.controller;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class ProfileTest {

	@Test
	public void testProfile() {
		//Create a new Profile
		Profile testProfile = new Profile();
		//Assert the the path is an empty path and the name is null
		assertNull(testProfile.getName());
		assertEquals(testProfile.getPath(), new File(""));
	}

	@Test
	public void testProfileStringFile() {
		//Create a new Profile
		Profile testProfile = new Profile("Testname",new File("Testfile"));
		//Assert the the path and the name are correct
		assertEquals(testProfile.getName(), "Testname");
		assertEquals(testProfile.getPath(), new File("Testfile"));

		//Create a new Profile with nulled name
		testProfile = new Profile(null,new File("Testfile"));
		//Assert the the path and the name are correct
		assertNull(testProfile.getName());
		assertEquals(testProfile.getPath(), new File("Testfile"));

		//Create a new Profile with null as path which should throw an exception
		try{
			testProfile = new Profile("Test",null);
			fail("Exception not thrown");
		}
		catch(Exception e){
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	@Test
	public void testGetSetName() {
		//Create a new Profile
		Profile testProfile = new Profile("Testname",new File("Testfile"));
		//Get the name
		assertEquals(testProfile.getName(),"Testname");
		//Set it to a different name
		testProfile.setName("Hallo Herr Stuckert");
		//Get the name again
		assertEquals(testProfile.getName(),"Hallo Herr Stuckert");
		//Set the name to null
		testProfile.setName(null);
		//Getter should return null
		assertNull(testProfile.getName());
	}

	@Test
	public void testGetSetPath() {
		//Create a new Profile
		Profile testProfile = new Profile("Testname",new File("Testfile"));
		//Get the File(path)
		assertEquals(testProfile.getPath(),new File("Testfile"));
		//Change the path
		testProfile.setPath(new File("Hallo Herr Rentschler"));
		//Path should have changed
		assertEquals(testProfile.getPath(), new File("Hallo Herr Rentschler"));
		//trying to set it null will result in an exception
		try{
			testProfile.setPath(null);
			fail("Exception not thrown");
		}
		catch(Exception e){
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	@Test
	public void testEqualsObject() {
		//Create three profiles
		Profile testProfile1 = new Profile("Testname",new File("Testfile1"));
		Profile testProfile2 = new Profile("Testname2",new File("Testfile2"));
		//testprofile3 shares the name with testProfile1
		Profile testProfile3 = new Profile("Testname3",new File("Testfile1"));
		//1 and 3 should be equal
		assertEquals(testProfile1,testProfile3);
		//1 and 2 should not be equal
		assertFalse(testProfile1.equals(testProfile2));
		//2 and 3 should not be equal
		assertFalse(testProfile2.equals(testProfile3));
		//assert with any other object should return false
		assertFalse(testProfile1.equals(new String("JUnit Tests finde ich blöd!")));
		assertFalse(testProfile1.equals(new File("HalloooDatei")));
		//assert with null will throw an exception
		try{
			testProfile1.equals(null);
			fail("Exception not thrown");
		}
		catch(Exception e){
			assertTrue(e instanceof IllegalArgumentException);
		}
	}
}
