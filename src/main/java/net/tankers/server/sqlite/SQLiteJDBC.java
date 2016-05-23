package net.tankers.server.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteJDBC {
	private Connection connection;
	
	public SQLiteJDBC() {
		connectToDatabase("database");
		setPragmas();
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
	
	public Connection getConnection() {
		return connection;
	}
	
	private void setPragmas() {
		try {
			Statement statement = connection.createStatement();
			String pragmas = "journal_mode=wal";
			statement.execute("pragma " + pragmas);
			statement.close();
		} catch (SQLException e) {
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
	

}
