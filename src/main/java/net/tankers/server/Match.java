package net.tankers.server;

import net.tankers.entity.Player;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by idrol on 20-04-2016.
 */
public class Match implements Runnable{
    private Player player1;
    private Player player2;
    private Server server;
    private boolean playerWon = false;
    private Queue<String> player1Queue = new ConcurrentLinkedQueue<>();
    private Queue<String> player2Queue = new ConcurrentLinkedQueue<>();

    public Match(Player player1, Player player2, Server server){
        this.player1 = player1;
        this.player2 = player2;
        this.server = server;
    }

    public void broadCast(String msg) {
        player1.write(msg);
        player2.write(msg);
    }

    public void receivedMessage(String message, Player player){
        if(player.equals(player1)){
            player1Queue.offer(message);
        }else if(player.equals(player2)){
            player2Queue.offer(message);
        }
    }

    private void processMessage(String message, Player player){

    }

    @Override
    public void run() {
        player1.write("match_found;"+player2.username);
        player2.write("match_found;"+player1.username);
        broadCast("match_map;default");
        while(!playerWon){
            if(!player1Queue.isEmpty()){
                for(String message: player1Queue){
                    processMessage(message, player1);
                }
            }
            if(!player2Queue.isEmpty()){
                for(String message: player2Queue){
                    processMessage(message, player2);
                }
            }
        }
    }
}
