package net.tankers.server.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLiteJDBC {
	private Connection connection;
	
	public SQLiteJDBC() {}
	
	public SQLiteJDBC(String databaseName) {
		connectToDatabase(databaseName);
	}
	
	public void connectToDatabase(String databaseName) {
		try {
			connection = 
					DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");
			System.out.println("Connected to " + databaseName + " successfully");
		} catch (SQLException e) {
			System.err.println("Couldn't connect to database '" + databaseName + "'");
			e.printStackTrace();
		}
	}
	
	public void disconnectFromDatabase() {
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
	
	public void insertInto(String tableName, String query) {
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
	
	//Exempelkod, bör adaptas sedan
	public ArrayList<String> selectFrom(String whatToSelect, String tableName) {
		ResultSet resultSet = null;
		ArrayList<String> result = new ArrayList<String>();
		
		try {
			Statement statement = connection.createStatement();
			
			String query = "SELECT " +
					whatToSelect +
					" FROM "
					+ tableName
					+ ";";
			
			System.out.println(query);
			
			resultSet = statement.executeQuery(query);
			
			int x = 1;
			while(resultSet.next()) {
				result.add("Name: " + resultSet.getString("username") +
						" Age: " + resultSet.getString("password"));
			}
			
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
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
	
	public void dropTable(String tableName) {
		try {
			Statement statement = connection.createStatement();
			String query = "DROP TABLE IF EXISTS " + tableName;
			statement.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		SQLiteJDBC db = new SQLiteJDBC();
		
		db.connectToDatabase("database");
		
		//db.dropTable("users");
		
		db.createTable("users", 
				"uniqueid integer PRIMARY KEY AUTOINCREMENT," +
				" username TEXT NOT NULL," +
				" password TEXT NOT NULL");
		
		//db.insertInto("users", "('username', 'password') VALUES ('testuser','1234')");

		boolean loggedin = db.validateUser("testuser", "1234");
		if(loggedin) {
			System.out.println("Logged in!");
		} else {
			System.out.println("Not logged in!");
		}
		
		db.disconnectFromDatabase();
	}
}
