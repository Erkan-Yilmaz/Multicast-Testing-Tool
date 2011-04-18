package com.spam.mctool.controller;

public class ErrorEvent {

    /**
     * The error message. Constructor and setter deny to set this to null.
     */
    private String errorMessage;

    /**
     * The error level.
     */
    private int errorLevel;

    /**
     * Default constructor will set the error level to 5 (highest)
     * and the error message to "Unknown error occured."
     */
    public ErrorEvent(){
        this.errorMessage = "Unknown error occured.";
        this.errorLevel = 5;
    }

    /**
     * Initialize an ErrorEvent with a message and an error level.
     * @param errorMessage The real message as string
     * @param errorLevel The error level as int (0 = lowest priority, 5 = highest priority)
     */
    public ErrorEvent(String errorMessage, int errorLevel){
        super();
        this.setErrorMessage(errorMessage);
        this.setErrorLevel(errorLevel);
    }

    /**
     * @return the error message as string
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Set the error message. Must not be null.
     * @param errorMessage
     */
    public void setErrorMessage(String errorMessage) {
        //errorMessage must not be null;
        if(errorMessage == null){
            throw new IllegalArgumentException();
        }

        this.errorMessage = errorMessage;
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
        //Don't allowe numbers bigger than 5
        else if(errorLevel >5){
            this.errorLevel = 5;
        }
        else{
            this.errorLevel = errorLevel;
        }
    }


}
