package net.tankers.server;

import net.tankers.entity.Player;

import java.util.concurrent.ConcurrentLinkedQueue;

public class PlayerQueueHandler {
	private static ConcurrentLinkedQueue<Player> playerQueue = new ConcurrentLinkedQueue<Player>();
	
	public static void printPlayers() {
		for (Player player : playerQueue) {
			System.out.println(player.username);
		}
	}
	
	public static void addPlayerToQueue(Player player) {
		playerQueue.offer(player);
	}
	
	public static int size() {
		return playerQueue.size();
	}
	
	public static Player pollPlayer() {
		return playerQueue.poll();
	}

	public static void removePlayer(Player player) {
		playerQueue.remove(player);
		System.out.println("Player " + player.username + " removed from queue");
	}
}
