package net.tankers.server.sqlite;

import java.sql.*;

import net.tankers.exceptions.DuplicateUserException;

public class SQLiteJDBC {
	private Connection connection;
	
	public SQLiteJDBC() {
		connectToDatabase("database");
		initializeUserTable();
	}
	
	private void connectToDatabase(String databaseName) {
		try {
			connection = 
					DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");
			System.out.println("Connected to " + databaseName + " successfully");
		} catch (SQLException e) {
			System.err.println("Couldn't connect to database '" + databaseName + "'");
			e.printStackTrace();
		}
	}
	
	public void closeConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/*
	 * Example of "rows" below:
	 * UNIQUEID INT PRIMARY KEY NOT NULL, NAME TEXT NOT NULL
	 * The "rows" above would create 2 rows (UNIQUEID and NAME)
	 */
	public void createTable(String tableName, String rows) {
		try {
			Statement statement = connection.createStatement();
			
			String sqlQuery = "CREATE TABLE IF NOT EXISTS " +tableName + "(" + rows + ")";
			System.out.println(sqlQuery);
			statement.execute(sqlQuery);
			statement.close();
			System.out.println("Created table " + tableName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void insertInto(String tableName, String query) {
		try {
			Statement statement = connection.createStatement();
			
			query = "INSERT INTO " +
					tableName +
					query;
			
			statement.executeUpdate(query);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void printAllUsers() {
		ResultSet resultSet = null;
		try {
			Statement statement = connection.createStatement();
			String query = "SELECT * FROM users";
			resultSet = statement.executeQuery(query);
			
			while(resultSet.next()) {
				System.out.println(resultSet.getString("username") + ":" + resultSet.getString("password"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean validateUser(String username, String password) {
		ResultSet resultSet;
		int result = 0;
		
		try {
			Statement statement = connection.createStatement();
			
			String query = "SELECT EXISTS(SELECT username, password "
					+ "FROM users "
					+ "WHERE username='" + username + "' AND password='" + password +"' LIMIT 1)";
			
			resultSet = statement.executeQuery(query);
			result = resultSet.getInt(1);
			
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
	
	public boolean isDuplicateUser(String username) {
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
	
	public void createUser(String username, String password) throws DuplicateUserException {
		if(!isDuplicateUser(username)) {
			insertInto("users", "('username', 'password') "
					+ "VALUES ('" + username + "','"+ password +"')");
		} else {
			throw new DuplicateUserException(username);
		}
	}
	
	public void deleteUser(String username) {
		
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
	
	private void initializeUserTable() {
		createTable("users", 
				"uniqueid integer PRIMARY KEY AUTOINCREMENT," +
				" username TEXT NOT NULL," +
				" password TEXT NOT NULL");
	}
	
	public static void main(String[] args) {
		SQLiteJDBC db = new SQLiteJDBC();
		
		db.connectToDatabase("database");
		db.initializeUserTable();
		
		//db.createUser("testuser", "1234");
		//db.createUser("testuser2", "1234");
		//db.createUser("testuser3", "1234");
		
		
		//db.deleteUser("testuser3");
		
		db.printAllUsers();
		
		boolean loggedin = db.validateUser("testuser3", "1234");
		if(loggedin) {
			System.out.println("Logged in!");
		} else {
			System.out.println("Not logged in!");
		}
		
		db.closeConnection();
	}
}
