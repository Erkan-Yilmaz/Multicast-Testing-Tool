package com.spam.mctool.intermediates;

public class ReceiverCreationException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;
	
	private ErrorType errorType;
	
	public static enum ErrorType { GROUP, PORT, NETWORKINTERFACE, ANALYZINGBEHAVIOR }
	
	public ReceiverCreationException(ErrorType type) {
		this.errorType = type;
	}
	
	public ErrorType getErrorType() {
		return this.errorType;
	}

}
