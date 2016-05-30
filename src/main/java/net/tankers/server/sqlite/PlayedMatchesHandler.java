package net.tankers.server.sqlite;

public class PlayedMatchesHandler {
    private SQLiteJDBC sqlite;

    public PlayedMatchesHandler(SQLiteJDBC sqlite) {
        this.sqlite = sqlite;
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

    private void initializeSessionPlayedMatchesTable() {
        sqlite.createTable("sessionplayedmatches",
                "uniqueid integer PRIMARY KEY AUTOINCREMENT," +
                        " playedMatches TEXT NOT NULL");
    }

    public void insertSessionPlayedMatches(String playedMatches) {
        sqlite.insertInto("sessionplayedmatches", "('playedMatches') "
                + "VALUES ('"+ playedMatches +"')");
    }
}
