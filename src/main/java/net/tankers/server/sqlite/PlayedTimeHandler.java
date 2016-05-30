package net.tankers.server.sqlite;

import net.tankers.server.sqlite.SQLiteJDBC;

public class PlayedTimeHandler {
	private SQLiteJDBC sqlite;
	
	public PlayedTimeHandler(SQLiteJDBC sqlite) {
		this.sqlite = sqlite;
		initializeSessionTimesTable();

	}
	
	public void insertSessionTime(String sessionTimeInSeconds) {
		sqlite.insertInto("sessiontimes", "('sessionTime') "
				+ "VALUES ('"+ sessionTimeInSeconds +"')");
	}
	
	private void initializeSessionTimesTable() {
		sqlite.createTable("sessiontimes", 
				"uniqueid integer PRIMARY KEY AUTOINCREMENT," +
				" sessionTime TEXT NOT NULL");
	}
}
