package net.tankers.server;

import net.tankers.exceptions.DuplicateUserException;
import net.tankers.server.sqlite.SQLiteJDBC;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserHandler {
	private SQLiteJDBC sqlite;
	private Connection connection;
	
	UserHandler(SQLiteJDBC sqlite) {
		this.sqlite = sqlite;
		this.connection = sqlite.getConnection();
		initializeUserTable();
	}
	
	public void printAllUsers() {
		ResultSet resultSet = null;
		try {
			Statement statement = connection.createStatement();
			String query = "SELECT * FROM users";
			resultSet = statement.executeQuery(query);
			
			System.out.println(">PRINT ALL USERS<");
			while(resultSet.next()) {
				System.out.println(resultSet.getString("username") + ":" + resultSet.getString("password"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void initializeUserTable() {
		sqlite.createTable("users", 
				"uniqueid integer PRIMARY KEY AUTOINCREMENT," +
				" username TEXT NOT NULL," +
				" password TEXT NOT NULL");
	}
	
	private void deleteUser(String username) {
		
		if(username == null)
			return;
		
		try {
			Statement statement = connection.createStatement();
			
			String query = "DELETE FROM users WHERE username IN "
					+ "(SELECT username FROM users WHERE username="
					+ "'"+ username +"' LIMIT 1)";
			
			statement.executeUpdate(query);
			
			statement.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void createUser(String username, String password) throws DuplicateUserException {
		if(!isDuplicateUser(username)) {
			sqlite.insertInto("users", "('username', 'password') "
					+ "VALUES ('" + username + "','"+ password +"')");
		} else {
			throw new DuplicateUserException(username);
		}
	}
	
	private boolean isDuplicateUser(String username) {
		ResultSet resultSet;
		int result = 0;
		
		try {
			Statement statement = connection.createStatement();
			
			String query = "SELECT EXISTS(SELECT username, password "
					+ "FROM users "
					+ "WHERE username='" + username + "' LIMIT 1)";
			
			resultSet = statement.executeQuery(query);
			result = resultSet.getInt(1);
			statement.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(result == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	private boolean validateUser(String username, String password) {
		ResultSet resultSet;
		int result = 0;
		
		try {
			Statement statement = connection.createStatement();
			
			String query = "SELECT EXISTS(SELECT username, password "
					+ "FROM users "
					+ "WHERE username='" + username + "' AND password='" + password +"' LIMIT 1)";
			
			resultSet = statement.executeQuery(query);
			result = resultSet.getInt(1);
			
			resultSet.close();
			statement.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(result == 1) {
			return true;
		} else {
			return false;
		}
	}
	
	boolean authenticateUser(String username, String password) {
    	if(username.length() >= 4 || password.length() >= 4) {
    		boolean auth = validateUser(username, password);
        	return auth;
    	} else {
    		return false;
    	}
    }
    
    String verifyRegistrationCredentials(String username, String password, String verifyPassword) {
    	if(username.length() >= 4 && password.length() >= 4) {
    		if(!isDuplicateUser(username)) {
        		if(password.equals(verifyPassword)) {
        			return "Success";
        		} else {
        			return "Passwords do not match";
        		}
        	} else {
        		return "A user with that name already exists";
        	}
    	} else {
    		return "Too short username or password, need to be 4 chars minimum";
    	}
    }
    
    void createNewUser(String username, String password) {
    	try {
			createUser(username, password);
		} catch (DuplicateUserException e) {
			e.printStackTrace();
		}
    }
}
