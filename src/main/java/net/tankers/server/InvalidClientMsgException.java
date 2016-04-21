package net.tankers.server;

@SuppressWarnings("serial")
public class InvalidClientMsgException extends Exception {
	
	public InvalidClientMsgException() {}
	
	public InvalidClientMsgException(String message) {
		super(message);
	}
	
	public InvalidClientMsgException(Throwable cause) {
		super(cause);
	}
	
	public InvalidClientMsgException(String message, Throwable cause) {
		super(message, cause);
	}
}
