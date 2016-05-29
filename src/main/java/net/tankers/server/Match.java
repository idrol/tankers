package net.tankers.server;

import net.tankers.entity.Player;
import net.tankers.entity.Shell;
import net.tankers.entity.Tank;
import net.tankers.exceptions.InvalidClientMsgException;
import net.tankers.utils.NetworkUtils;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.lwjgl.Sys;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by idrol on 20-04-2016.
 *
 * 1 meter = 30 pixels
 *
 */
public class Match extends Thread{
    public static final int WON = 1;
    public static final int FORFEIT = 2;

    public static final int PIXELS_PER_METER = 30;

    private Player player1;
    private Player player2;
    private Server server;
    private boolean playerWon = false;
    private Queue<String> player1Queue = new ConcurrentLinkedQueue<>();
    private Queue<String> player2Queue = new ConcurrentLinkedQueue<>();
    private Tank tank1, tank2;
    private List<Shell> shells = new LinkedList<>();
    private long lastFrame;

    private World world = new World(new Vec2(0, 0));
    private Set<Body> bodies = new HashSet<>();

    public Match(Player player1, Player player2, Server server){
        this.player1 = player1;
        this.player2 = player2;
        this.server = server;
        world.setAllowSleep(true);
        world.setContactListener(new ShellContactListener());
    }

    public World getWorld() {
        return world;
    }

    public Set<Body> getBodies() {
        return bodies;
    }

    public void addShell(Shell shell) {
        shells.add(shell);
        broadCast(shell.sync());
    }

    public void removeShell(Shell shell) {
        shells.remove(shell);
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
        player1.write("match_found;"+player2.username);
        player2.write("match_found;"+player1.username);
    }

    public static float toMeters(float pixels) {
        return pixels/PIXELS_PER_METER;
    }

    public static float toPixels(float meters) {
        return meters * PIXELS_PER_METER;
    }

    @Override
    public void run() {
        broadCast("match_map;default");
        tank1 = new Tank(server, this, player1, 100, 100);
        tank2 = new Tank(server, this, player2, 200, 100);
        tank1.setup(world, bodies);
        tank2.setup(world, bodies);
        broadCast(tank1.sync());
        broadCast(tank2.sync());
        lastFrame = getTime();
        while(!playerWon){
            update();
        }
    }

    // This player won the game
    public void endGame(Player winner, Player loser, int reason) {
        System.out.println("Ending game");
        playerWon = true;
        player1.isInMatch = false;
        player2.isInMatch = false;
        player1.match = null;
        player2.match = null;
        winner.write("match_result;won:" + reason);
        loser.write("match_result;lost:" + reason);
    }

    public void update() {
        float delta = getDelta();
        world.step(1f/60f, 8, 3);
        tank1.updateServer(delta);
        tank2.updateServer(delta);
        for(Shell shell: new LinkedList<>(shells)){
            shell.updateServer(delta);
        }
        String message;
        while(!player1Queue.isEmpty()){
            message = player1Queue.poll();
            processMessage(message, player1);
        }
        while(!player2Queue.isEmpty()){
            message = player2Queue.poll();
            processMessage(message, player2);
        }

        long timeToSleep = (long)16.6666667 - (getTime() - lastFrame);
        if(timeToSleep < 0) timeToSleep = 0;
        try {
            Thread.sleep(timeToSleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Player getOtherPlayer(Player player) {
        if(player1 == player) {
            return player2;
        }else{
            return player1;
        }
    }
}
