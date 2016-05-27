package net.tankers.server;

import net.tankers.server.sqlite.SQLiteJDBC;

public class AnalyticsHandler {
	private SQLiteJDBC sqlite;
	
	AnalyticsHandler(SQLiteJDBC sqlite) {
		this.sqlite = sqlite;
		initializeSessionTimesTable();
		initializeSessionPlayedMatchesTable();
	}
	
	public void insertSessionTime(String sessionTimeInSeconds) {
		sqlite.insertInto("sessiontimes", "('sessionTime') "
				+ "VALUES ('"+ sessionTimeInSeconds +"')");
	}
	
	public void insertSessionPlayedMatches(String playedMatches) {
		sqlite.insertInto("playedmatches", "('playedMatches') "
				+ "VALUES ('"+ playedMatches +"')");
	}
	
	private void initializeSessionTimesTable() {
		sqlite.createTable("sessiontimes", 
				"uniqueid integer PRIMARY KEY AUTOINCREMENT," +
				" sessionTime TEXT NOT NULL");
	}
	
	private void initializeSessionPlayedMatchesTable() {
		sqlite.createTable("playedmatches", 
				"uniqueid integer PRIMARY KEY AUTOINCREMENT," +
				" playedMatches TEXT NOT NULL");
	}
}
