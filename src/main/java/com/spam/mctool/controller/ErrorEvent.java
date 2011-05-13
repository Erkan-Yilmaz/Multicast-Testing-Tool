package com.spam.mctool.controller;

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
        this.localizedMessageIdentifier = "Controller.unknownError.text";
        this.additionalErrorMessage = "";
        this.errorLevel = 5;
    }

    /**
     * Initialize an ErrorEvent with an localized message identifiern, an additional message and an error level.
     * @param errorLevel The error level as int (0 = lowest priority, 5 = highest priority)
     * @param localizedErrorMessage An localized message identifier(string).
     * @param additionalErrorMessage An additional message as string.
     */
    public ErrorEvent(int errorLevel, String localizedErrorMessage, String additionalErrorMessage){
        super();
        this.setLocalizedMessageIdentifier(localizedErrorMessage);
        this.setAdditionalErrorMessage(additionalErrorMessage);
        this.setErrorLevel(errorLevel);
    }

    /**
     * @return the additional error message as string
     */
    public String getAdditionalErrorMessage() {
        return additionalErrorMessage;
    }

    /**
     * Set the additional error message.
     * @param additionalErrorMessage
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
     * @return The error log level (0 <= x <= 5).
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

	/**
	 * Get the localized message identifier.
	 * @return the localized message identifier.
	 */
	public String getLocalizedMessageIdentifier() {
		return localizedMessageIdentifier;
	}

	/**
	 * Get the localized message. The set identifier is used to retrieve the localized version of the message.
	 * @return "" if localization not found, otherwise the localized version of the message.
	 */
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

	/**
	 * Set the localized message identifier.
	 * @param localizedMessageIdentifier The localized message identifier.
	 */
	public void setLocalizedMessageIdentifier(String localizedMessageIdentifier) {
		if(localizedMessageIdentifier == null){
			throw new IllegalArgumentException();
		}
		this.localizedMessageIdentifier = localizedMessageIdentifier;
	}

	/**
	 * Get the complete message. This will combine the localized version of the message with the additional error message using a ": ".
	 * @return The complete error message.
	 */
	public String getCompleteMessage() {
		String localizedErrorMessage = this.getLocalizedMessage();
		//if we have no identifier, print the additional message
		if(localizedErrorMessage.compareTo("") == 0 || localizedErrorMessage == null){
			return this.localizedMessageIdentifier + ": " + getAdditionalErrorMessage();
		}
		//if the additional message is empty, print the localized version only
		if(additionalErrorMessage.compareTo("") == 0){
			return localizedErrorMessage;
		}
		//otherwise combine both
		return localizedErrorMessage + ": " + additionalErrorMessage;
	}


}
