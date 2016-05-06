package net.tankers.server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.tankers.entity.Player;

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
}
