package net.tankers.client.analytics;

public class TimePlayed {
	private static long gameStartTime;
	private static long gameQuitTime;

	public static long getGameStartTime() {
		return gameStartTime;
	}

	public static void setGameStartTime(long gameStartTime) {
		TimePlayed.gameStartTime = gameStartTime;
	}

	public static long getGameQuitTime() {
		return gameQuitTime;
	}

	public static void setGameQuitTime(long gameQuitTime) {
		TimePlayed.gameQuitTime = gameQuitTime;
	}
	
	public static double getTimePlayed() {
		return (TimePlayed.getGameQuitTime() - TimePlayed.getGameStartTime())/1000.0;
	}
}
