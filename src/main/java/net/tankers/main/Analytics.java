package net.tankers.main;

public class Analytics {
	private static long gameStartTime;
	private static long gameQuitTime;

	public static long getGameStartTime() {
		return gameStartTime;
	}

	public static void setGameStartTime(long gameStartTime) {
		Analytics.gameStartTime = gameStartTime;
	}

	public static long getGameQuitTime() {
		return gameQuitTime;
	}

	public static void setGameQuitTime(long gameQuitTime) {
		Analytics.gameQuitTime = gameQuitTime;
	}
	
	public static double getTimePlayed() {
		return (Analytics.getGameQuitTime() - Analytics.getGameStartTime())/1000.0;
	}
}
