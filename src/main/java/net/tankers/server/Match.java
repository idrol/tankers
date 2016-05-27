package net.tankers.server;

import net.tankers.entity.Player;
import net.tankers.entity.Tank;
import net.tankers.exceptions.InvalidClientMsgException;
import net.tankers.utils.NetworkUtils;
import org.lwjgl.Sys;

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
    private long lastFrame;

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

    private long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    public float getDelta() {
        long time = getTime();
        float delta = time - lastFrame;
        lastFrame = time;
        return delta;
    }

    private void processMessage(String message, Player player){
        try {
            String[] messageArray = NetworkUtils.constructValidMessage(message);

            switch (messageArray[0]) {
                case "key_pressed":
                    if(player.equals(player1)){
                        tank1.pressed(messageArray[1]);
                    }else if(player.equals(player2)) {
                        tank2.pressed(messageArray[1]);
                    }
                    break;
                case "key_released":
                    if(player.equals(player1)){
                        tank1.released(messageArray[1]);
                    }else if(player.equals(player2)) {
                        tank2.released(messageArray[1]);
                    }
            }
        } catch (InvalidClientMsgException e) {
            System.err.println("A client tried to send an invalid message " + message);
        }
    }

    public void init() {
        broadCast("notification;Match found!");
        player1.write("match_found;"+player2.username);
        player2.write("match_found;"+player1.username);
    }

    @Override
    public void run() {
        broadCast("match_map;default");
        tank1 = new Tank(server, this, player1, 100, 100);
        tank2 = new Tank(server, this, player2, 200, 100);
        broadCast(tank1.sync());
        broadCast(tank2.sync());
        lastFrame = getTime();
        while(!playerWon){
            update();
        }
    }

    public void update() {
        float delta = getDelta();
        tank1.updateServer(delta);
        tank2.updateServer(delta);
        String message;
        while(!player1Queue.isEmpty()){
            message = player1Queue.poll();
            processMessage(message, player1);
        }
        while(!player2Queue.isEmpty()){
            message = player2Queue.poll();
            processMessage(message, player2);
        }
        System.out.println(delta);
        long timeToSleep = (long)16.6666667 - (long)delta;
        if(timeToSleep < 0) timeToSleep = 0;
        try {
            Thread.sleep(timeToSleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
