package net.tankers.server.sqlite;

/**
 * Created by Strongdoctor on 30-05-2016.
 */
public class Match {
    private String winner, loser;
    private long duration;

    Match(long duration, String winner, String loser) {
        this.duration = duration;
        this.winner = winner;
        this.loser = loser;
    }

    public String getWinner() {
        return winner;
    }

    public String getLoser() {
        return loser;
    }

    public long getDuration() {
        return duration;
    }
}
