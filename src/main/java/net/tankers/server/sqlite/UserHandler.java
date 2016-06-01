package net.tankers.server.sqlite;

import net.tankers.exceptions.DuplicateUserException;
import net.tankers.server.sqlite.SQLiteJDBC;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.mindrot.jbcrypt.BCrypt;

public class UserHandler {
	private final SQLiteJDBC sqlite;
	private final Connection connection;
	private static final String ADMIN_USERNAME = "admin";
	private static final String ADMIN_PASSWORD = "UltraSecretPass";
	
	public UserHandler(SQLiteJDBC sqlite) {
		this.sqlite = sqlite;
		this.connection = sqlite.getConnection();
		initializeUserTable();
		createAdminUser();
	}
	
	public void printAllUsers() {
		ResultSet resultSet;
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
			String hashedpassword = BCrypt.hashpw(password, BCrypt.gensalt(10));
			
			sqlite.insertInto("users", "('username', 'password') "
					+ "VALUES ('" + username + "','"+ hashedpassword +"')");
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
		String hashedPass = null;
		Statement statement;
		try {
			statement = connection.createStatement();

			String query = "SELECT * "
					+ "FROM users "
					+ "WHERE username='" + username + "'";

			resultSet = statement.executeQuery(query);
			
			while (resultSet.next()) {
				hashedPass = resultSet.getString("password");
			}
			
			resultSet.close();
			statement.close();
			System.out.println("Username: " + username + "HashedPass: " + hashedPass);
			
			if (hashedPass != null) {
				if (BCrypt.checkpw(password, hashedPass)) {
					return true;
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean authenticateUser(String username, String password) {
    	if(username.length() >= 4 || password.length() >= 4) {
    		boolean auth = validateUser(username, password);
        	return auth;
    	} else {
    		return false;
    	}
    }
    
    public String verifyRegistrationCredentials(String username, String password, String verifyPassword) {
    	if(username.length() >= 4 && password.length() >= 4) {
    		if(!isDuplicateUser(username)) {
        		if(password.equals(verifyPassword)) {
        			return "success";
        		} else {
        			return "passwordsnotmatching";
        		}
        	} else {
        		return "duplicateuser";
        	}
    	} else {
    		return "tooshortuserpass";
    	}
    }
    
    public void createNewUser(String username, String password) {
    	try {
			createUser(username, password);
		} catch (DuplicateUserException e) {
			System.err.println("User '" + username + "' already exists");
		}
    }

	private void createAdminUser() {
		if (isDuplicateUser("admin"))
			deleteUser(ADMIN_USERNAME);

		createNewUser(ADMIN_USERNAME,ADMIN_PASSWORD);
	}

	public String getNumberOfUsers() {
		ResultSet resultSet;
		Statement statement;
		long numberOfUsers = 0;

		try {
			statement = connection.createStatement();
			String query = "SELECT COUNT(*) as total FROM users";

			resultSet = statement.executeQuery(query);

			while (resultSet.next()) {
				numberOfUsers = resultSet.getLong("total");
			}

			resultSet.close();
			statement.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Long.toString(numberOfUsers);
	}
}
