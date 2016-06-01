package net.tankers.server.sqlite;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class PlayedMatchesHandler {
    private final SQLiteJDBC sqlite;
    private final Connection connection;

    public PlayedMatchesHandler(SQLiteJDBC sqlite) {
        this.sqlite = sqlite;
        this.connection = sqlite.getConnection();
        initializePlayedMatchesTable();
        initializeSessionPlayedMatchesTable();
    }

    private void initializePlayedMatchesTable() {
        sqlite.createTable("matches",
                "uniqueid integer PRIMARY KEY AUTOINCREMENT, " +
                "duration TEXT NOT NULL, " +
                "winner TEXT NOT NULL, " +
                "loser TEXT NOT NULL");
    }

    public void insertPlayedMatch(long duration, String winner, String loser) {
        sqlite.insertInto("matches", "('duration','winner','loser') "
                + "VALUES ('" + duration + "', '" + winner + "', '" + loser + "')");
    }

    public ArrayList<Match> getUserMatches(String username) {
        ResultSet resultSet;
        ArrayList<Match> matches = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();

            String query = "SELECT duration, winner, loser FROM matches WHERE winner = '"
                    + username
                    + "' OR loser = '"
                    + username
                    + "'";

            resultSet = statement.executeQuery(query);

            while(resultSet.next()) {
                long duration = resultSet.getLong("duration");
                String winner = resultSet.getString("winner");
                String loser = resultSet.getString("loser");
                matches.add(new Match(duration, winner, loser));
            }

            resultSet.close();
            statement.close();
            return matches;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matches;
    }

    public int[] getUserMatchStats(ArrayList<Match> matches, String username) {
        int totalMatches = matches.size();
        int wonMatches = 0;

        for(Match match : matches) {
            if(match.getWinner().equalsIgnoreCase(username))
                wonMatches++;
        }

        return new int[]{totalMatches, wonMatches};
    }

    private void initializeSessionPlayedMatchesTable() {
        sqlite.createTable("sessionplayedmatches",
                "uniqueid integer PRIMARY KEY AUTOINCREMENT," +
                        " playedMatches TEXT NOT NULL");
    }

    public void insertSessionPlayedMatches(String playedMatches) {
        sqlite.insertInto("sessionplayedmatches", "('playedMatches') "
                + "VALUES ('"+ playedMatches +"')");
    }

    public String getAverageMatchDuration() {
        ResultSet resultSet;
        Statement statement;
        ArrayList<Long> timeList = new ArrayList<>();
        String averageMatchTime = "0.000s";
        long duration;
        long totalDuration = 0;

        try {
            statement = connection.createStatement();
            String query = "SELECT duration "
                    + "FROM matches";

            resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                duration = resultSet.getLong("duration");
                timeList.add(duration);
            }

            for (Long time : timeList) {
                totalDuration += time;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (totalDuration/timeList.size())/1000f + "s";
    }

    public String getNumberOfMatches() {
        ResultSet resultSet;
        Statement statement;
        long numberOfMatches = 0;

        try {
            statement = connection.createStatement();
            String query = "SELECT COUNT(*) as total FROM matches";

            resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                numberOfMatches = resultSet.getLong("total");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Long.toString(numberOfMatches);
    }
}
