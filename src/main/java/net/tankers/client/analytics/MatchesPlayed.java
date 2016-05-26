package net.tankers.client.analytics;

public class MatchesPlayed {
	private static int matchesPlayed = 0;

	public static int getMatchesPlayed() {
		return matchesPlayed;
	}
	
	public static void incrementMatchesPlayed() {
		MatchesPlayed.matchesPlayed += 1;
	}
}
