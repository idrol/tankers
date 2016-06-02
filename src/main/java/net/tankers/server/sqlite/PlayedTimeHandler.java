package net.tankers.server.sqlite;

import net.tankers.server.sqlite.SQLiteJDBC;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class PlayedTimeHandler {
	private SQLiteJDBC sqlite;
	private final Connection connection;
	
	public PlayedTimeHandler(SQLiteJDBC sqlite) {
		this.sqlite = sqlite;
		this.connection = sqlite.getConnection();
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

	public String getAverageSessionTime() {
		ResultSet resultSet;
		Statement statement;
		ArrayList<Long> timeList = new ArrayList<>();
		long totalTime = 0;
		String averageSessionTime = "0.000s";

		try {
			long startTime, endTime;

			statement = connection.createStatement();

			String query = "SELECT * "
					+ "FROM sessiontimes";

			resultSet = statement.executeQuery(query);

			while (resultSet.next()) {
				startTime = resultSet.getLong("startTime");
				endTime = resultSet.getLong("endTime");

				if(endTime != 0)
					timeList.add((endTime-startTime));
			}

			for(Long time : timeList) {
				totalTime += time;
			}

			resultSet.close();
			statement.close();

			if(timeList.size() > 0) {
				averageSessionTime = (totalTime/timeList.size())/1000f + "s";
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return averageSessionTime;
	}
}
