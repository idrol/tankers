package net.tankers.server.sqlite;

public class DuplicateUserException extends Exception {
	private static final long serialVersionUID = -7109853582586230005L;

	public DuplicateUserException() {}
	
	public DuplicateUserException(String username) {
		System.err.println("The user '" + username + "' already exists" + System.getProperty("line.separator"));
	}
	
	public DuplicateUserException(Throwable cause) {
		super(cause);
	}
	
	public DuplicateUserException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
