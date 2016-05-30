package net.tankers.server.sqlite;

import net.tankers.server.sqlite.SQLiteJDBC;

public class PlayedTimeHandler {
	private SQLiteJDBC sqlite;
	
	public PlayedTimeHandler(SQLiteJDBC sqlite) {
		this.sqlite = sqlite;
		initializeSessionTimesTable();

	}
	
	public void insertSessionTime(String startTime, String endTime) {
		sqlite.insertInto("sessiontimes", "('startTime', 'endTime') "
				+ "VALUES ('" + startTime + "','"+ endTime +"')");
	}
	
	private void initializeSessionTimesTable() {
		sqlite.createTable("sessiontimes", 
				"uniqueid integer PRIMARY KEY AUTOINCREMENT, " +
				"startTime TEXT NOT NULL, " +
				"endTime TEXT NOT NULL");
	}
}
