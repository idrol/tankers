package net.tankers.server;

import net.tankers.entity.Player;
import net.tankers.entity.Tank;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by idrol on 20-04-2016.
 */
public class Match extends Thread{
    private Player player1;
    private Player player2;
    private Server server;
    private boolean playerWon = false;
    private Queue<String> player1Queue = new ConcurrentLinkedQueue<>();
    private Queue<String> player2Queue = new ConcurrentLinkedQueue<>();
    private Tank tank1, tank2;

    public Match(Player player1, Player player2, Server server){
        this.player1 = player1;
        this.player2 = player2;
        this.server = server;
    }

    public boolean hasPlayer(Player player) {
        return player2 == player || player1 == player;
    }

    public void broadCast(List<String> msgs) {
        for(String msg: msgs){
            player1.write(msg);
            player2.write(msg);
        }
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

    public void init() {
        broadCast("notification;Match found!");
        player1.write("match_found;"+player2.username);
        player2.write("match_found;"+player1.username);
    }

    @Override
    public void run() {
        broadCast("match_map;default");
        tank1 = new Tank(server, player1);
        tank2 = new Tank(server, player2);
        broadCast(tank1.sync());
        broadCast(tank2.sync());
        while(!playerWon){
            update();
        }
    }

    public void update() {
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
