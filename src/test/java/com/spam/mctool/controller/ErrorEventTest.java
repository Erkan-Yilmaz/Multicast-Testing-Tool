package com.spam.mctool.controller;

import static org.junit.Assert.*;

import org.junit.Test;

public class ErrorEventTest {

	@Test
	public void testErrorEvent() {
		//Create an default ErrorEvent
		ErrorEvent event = new ErrorEvent();
		//Level will be set to FATAL
		assertEquals(event.getErrorLevel(),ErrorEventManager.FATAL);
		//Additional Message will be empty
		assertEquals(event.getAdditionalErrorMessage(),"");
		//The Identifier will be set to "Controller.unknownError.text"
		assertEquals(event.getLocalizedMessageIdentifier(),"Controller.unknownError.text");
	}

	@Test
	public void testErrorEventIntStringString() {
		//Create an default ErrorEvent
		ErrorEvent event = new ErrorEvent(ErrorEventManager.ERROR,"testidentifier","testmessage");
		//Level will be ERROR
		assertEquals(event.getErrorLevel(),ErrorEventManager.ERROR);
		//Additional Message set to the defined string
		assertEquals(event.getAdditionalErrorMessage(),"testmessage");
		//The Identifier will be set to the string defined
		assertEquals(event.getLocalizedMessageIdentifier(),"testidentifier");

		//Error event with null as additional message
		event = new ErrorEvent(ErrorEventManager.ERROR,"testidentifier",null);
		//Level will be ERROR
		assertEquals(event.getErrorLevel(),ErrorEventManager.ERROR);
		//Additional Message will be empty
		assertEquals(event.getAdditionalErrorMessage(),"");
		//The Identifier will be set to "Controller.unknownError.text"
		assertEquals(event.getLocalizedMessageIdentifier(),"testidentifier");

		//Event with null as identifier will throw an exception
		try{
			event = new ErrorEvent(ErrorEventManager.ERROR,null,"Test");
			fail("Exception not thrown");
		}
		catch(Exception e){
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	@Test
	public void testGetSetAdditionalErrorMessage() {
		//Create a new Error Event
		ErrorEvent event = new ErrorEvent();
		//Set a message
		event.setAdditionalErrorMessage("Test");
		assertEquals(event.getAdditionalErrorMessage(), "Test");
		//Set another message
		event.setAdditionalErrorMessage("JUnit ist ziemlich seltsam!");
		assertEquals(event.getAdditionalErrorMessage(), "JUnit ist ziemlich seltsam!");
		//Set it null will result in an empty string
		event.setAdditionalErrorMessage(null);
		assertEquals(event.getAdditionalErrorMessage(), "");
	}

	@Test
	public void testGetSetErrorLevel() {
		//Create a new Error Event
		ErrorEvent event = new ErrorEvent();
		//Set the level to 1
		event.setErrorLevel(1);
		assertEquals(event.getErrorLevel(),1);
		//Set it to 3
		event.setErrorLevel(3);
		assertEquals(event.getErrorLevel(),3);
		//Set it to 5
		event.setErrorLevel(5);
		assertEquals(event.getErrorLevel(),5);
		//Set it to x>5 will result in 5
		event.setErrorLevel(10);
		assertEquals(event.getErrorLevel(),5);
		//Set it to x<0 will result in 0
		event.setErrorLevel(-2);
		assertEquals(event.getErrorLevel(),0);
	}

	@Test
	public void testGetSetLocalizedMessageIdentifier() {
		//Create a new Error Event
		ErrorEvent event = new ErrorEvent();
		//Set the identifier to some value
		event.setLocalizedMessageIdentifier("Test");
		assertEquals(event.getLocalizedMessageIdentifier(),"Test");
		//Set the identifier to some other value
		event.setLocalizedMessageIdentifier("some other value");
		assertEquals(event.getLocalizedMessageIdentifier(),"some other value");
		//Set it to null will result in an exception
		try{
			event.setLocalizedMessageIdentifier(null);
			fail("Exception not thrown.");
		}
		catch(Exception e){
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	@Test
	public void testGetSetLocalizedMessage(){
		//Create a new Error Event
		ErrorEvent event = new ErrorEvent();
		//Set the message identifier to some well known value
		event.setLocalizedMessageIdentifier("Controller.profileLoadingError.text");
		//compare it with the original
		assertEquals(event.getLocalizedMessage(),  java.util.ResourceBundle.getBundle("internationalization/Bundle").getString("Controller.profileLoadingError.text"));
		//Set it to some unknown value
		event.setLocalizedMessageIdentifier("What makes sis identifier?");
		//will result in an empty string
		assertEquals(event.getLocalizedMessage(),"");
	}

	@Test
	public void testGetCompleteMessage() {
		//Create a new Error Event
		ErrorEvent event = new ErrorEvent();
		//Set the message identifier to some well known value
		event.setLocalizedMessageIdentifier("Controller.profileLoadingError.text");
		//compare it with the expected output
		assertEquals(event.getCompleteMessage(),  java.util.ResourceBundle.getBundle("internationalization/Bundle").getString("Controller.profileLoadingError.text"));
		//add an additional message
		event.setAdditionalErrorMessage("Test");
		//compare it with the expected output
		assertEquals(event.getCompleteMessage(),  java.util.ResourceBundle.getBundle("internationalization/Bundle").getString("Controller.profileLoadingError.text") + ": Test" );

	}

}
