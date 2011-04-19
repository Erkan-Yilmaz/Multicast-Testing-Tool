package com.spam.mctool.controller;

import org.omg.CORBA.LocalObject;

public class ErrorEvent {

    /**
     * The error message. Constructor and setter, denied to set this to null.
     */
    private String additionalErrorMessage;
    
    /**
     * The key for the message error message. Constructor and setter, denied to set this to null.
     */
    private String localizedMessageIdentifier;
    
    /**
     * The error level.
     */
    private int errorLevel;

    /**
     * Default constructor will set the error level to 5 (highest)
     * and the error message to "Unknown error occured."
     */
    public ErrorEvent(){
        this.localizedMessageIdentifier = "";
        this.additionalErrorMessage = "Unknown error occured.";
        this.errorLevel = 5;
    }

    /**
     * Initialize an ErrorEvent with a message and an error level.
     * @param errorMessage The real message as string
     * @param errorLevel The error level as int (0 = lowest priority, 5 = highest priority)
     */
    public ErrorEvent(int errorLevel, String localizedErrorMessage, String additionalErrorMessage){
        super();
        this.setLocalizedMessageIdentifier(localizedErrorMessage);
        this.setAdditionalErrorMessage(additionalErrorMessage);
        this.setErrorLevel(errorLevel);
    }

    /**
     * @return the error message as string
     */
    public String getAdditionalErrorMessage() {
        return additionalErrorMessage;
    }

    /**
     * Set the error message. If set to null it is automatically set to "".
     * @param errorMessage
     */
    public void setAdditionalErrorMessage(String additionalErrorMessage) {
        //errorMessage must not be null;
        if(additionalErrorMessage == null){
            this.additionalErrorMessage = "";
        }
        else{
        	this.additionalErrorMessage = additionalErrorMessage;
        }
    }

    /**
     * Get the set error level for this error event.
     * @return
     */
    public int getErrorLevel() {
        return errorLevel;
    }

    /**
     * Set the error level as int (0 = lowest priority, 5 = highest priority).
     * @param errorLevel If <0 it is set to 0. if >5 it is set to 5.
     */
    public void setErrorLevel(int errorLevel) {
        //Don't allow negative numbers
        if(errorLevel < 0){
            this.errorLevel = 0;
        }
        //Don't allow numbers bigger than 5
        else if(errorLevel >5){
            this.errorLevel = 5;
        }
        else{
            this.errorLevel = errorLevel;
        }
    }

	public String getLocalizedMessageIdentifier() {
		return localizedMessageIdentifier;
	}
	
	public String getLocalizedMessage(){
		String localizedMessage;
		try{
	        localizedMessage = java.util.ResourceBundle.getBundle("internationalization/Bundle").getString(localizedMessageIdentifier);
		}
		catch(Exception e){
			localizedMessage = "";
		}
		return localizedMessage;
	}

	public void setLocalizedMessageIdentifier(String localizedMessageIdentifier) {
		if(localizedMessageIdentifier == null){
			throw new IllegalArgumentException();
		}
		this.localizedMessageIdentifier = localizedMessageIdentifier;
	}
	
	public String getCompleteMessage() {
		String localizedErrorMessage = this.getLocalizedMessage();
		if(localizedErrorMessage.compareTo("") == 0 || localizedErrorMessage == null){
			return getAdditionalErrorMessage();
		}
		if(additionalErrorMessage.compareTo("") == 0){
			return localizedErrorMessage;
		}
		return localizedErrorMessage + ": " + additionalErrorMessage;
	}
    

}
