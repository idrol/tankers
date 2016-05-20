package net.tankers.server;

import java.sql.Connection;

import net.tankers.server.sqlite.SQLiteJDBC;

public class AnalyticsHandler {
	private SQLiteJDBC sqlite;
	private Connection connection;
	
	AnalyticsHandler(SQLiteJDBC sqlite) {
		this.sqlite = sqlite;
		this.connection = sqlite.getConnection();
		initializeSessionTimesTable();
		initializeSessionPlayedMatchesTable();
	}
	
	public void insertSessionTime(String username, String sessionTimeInSeconds) {
		sqlite.insertInto("sessiontimes", "('username', 'sessionTime') "
				+ "VALUES ('" + username + "','"+ sessionTimeInSeconds +"')");
	}
	
	public void insertSessionPlayedMatches(String username, String playedMatches) {
		sqlite.insertInto("playedmatches", "('username', 'playedMatches') "
				+ "VALUES ('" + username + "','"+ playedMatches +"')");
	}
	
	private void initializeSessionTimesTable() {
		sqlite.createTable("sessiontimes", 
				"uniqueid integer PRIMARY KEY AUTOINCREMENT," +
				" username TEXT NOT NULL," +
				" sessionTime TEXT NOT NULL");
	}
	
	private void initializeSessionPlayedMatchesTable() {
		sqlite.createTable("playedmatches", 
				"uniqueid integer PRIMARY KEY AUTOINCREMENT," +
				" username TEXT NOT NULL," +
				" playedMatches TEXT NOT NULL");
	}
}
