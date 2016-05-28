package net.tankers.entity;

import io.netty.channel.Channel;
import net.tankers.client.Client;
import net.tankers.server.Match;
import net.tankers.server.Server;
import net.tankers.utils.NetworkUtils;
import org.jbox2d.common.Vec2;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.List;

/**
 * Created by idrol on 15-04-2016.
 */
public class Tank extends NetworkedEntity {

    private Player player;
    private Match match;
    private boolean moveForward = false;
    private boolean moveBackward = false;
    private boolean rotateLeft = false;
    private boolean rotateRight = false;

    public Tank(Integer instanceID) {
        super(instanceID);
    }

    public Tank(Server server, Match match, Player player, int x, int y) {
        super(server);
        this.player = player;
        this.x = x;
        this.y = y;
        this.match = match;
        sizeX = 30;
        sizeY = 30;
    }


    @Override
    public void decodeDataClient(String[] data) {
        if(data[0].equals("posX")){
            x = Integer.parseInt(data[1]);
        }else if(data[0].equals("posY")){
            y = Integer.parseInt(data[1]);
        }else if(data[0].equals("sizeX")){
            sizeX = Integer.parseInt(data[1]);
        }else if(data[0].equals("sizeY")){
            sizeY = Integer.parseInt(data[1]);
        }else if(data[0].equals("rotZ")) {
            rotZ = Integer.parseInt(data[1]);
        }
    }

    @Override
    public Entity setSize(int sizeX, int sizeY) {
        super.setSize(sizeX, sizeY);
        match.broadCast(NetworkUtils.encodeBase(this, NetworkUtils.UPDATE) + "sizeX:" + sizeX);
        match.broadCast(NetworkUtils.encodeBase(this, NetworkUtils.UPDATE) + "sizeY:" + sizeY);
        return this;
    }

    @Override
    public Entity setPos(int x, int y) {
        super.setPos(x, y);
        match.broadCast(NetworkUtils.encodeBase(this, NetworkUtils.UPDATE) + "posX:" + x);
        match.broadCast(NetworkUtils.encodeBase(this, NetworkUtils.UPDATE) + "posY:" + y);
        return this;
    }

    @Override
    public Entity setRot(int z) {
        super.setRot(z);
        match.broadCast(NetworkUtils.encodeBase(this, NetworkUtils.UPDATE) + "rotZ:" + rotZ);
        return this;
    }

    @Override
    public void decodeDataServer(String[] data) {

    }

    @Override
    public String[] encodeData(String variable) {
        return new String[0];
    }

    @Override
    public List<String> sync(List<String> msg) {
        msg.add(NetworkUtils.encodeBase(this, NetworkUtils.UPDATE) + "posX:" + x);
        msg.add(NetworkUtils.encodeBase(this, NetworkUtils.UPDATE) + "posY:" + y);
        msg.add(NetworkUtils.encodeBase(this, NetworkUtils.UPDATE) + "sizeX:" + sizeX);
        msg.add(NetworkUtils.encodeBase(this, NetworkUtils.UPDATE) + "sizeY:" + sizeY);
        msg.add(NetworkUtils.encodeBase(this, NetworkUtils.UPDATE) + "rotZ:" + rotZ);
        return msg;
    }

    @Override
    public void updateServer(float delta) {
        float xVel = 0;
        float yVel = 0;
        float speed = 0.5f;
        if(moveForward != moveBackward) {
            if(moveForward) {
                xVel -= speed * Math.sin(Math.toDegrees(body.getAngle()));
                yVel -= speed * Math.cos(Math.toDegrees(body.getAngle()));
                //setPos(x, y - (int)(0.25f*delta));
            }else {
                xVel += speed * Math.sin(Math.toDegrees(body.getAngle()));
                yVel += speed * Math.cos(Math.toDegrees(body.getAngle()));
                //setPos(x, y + (int) (0.25f * delta));
            }
        }
        float angularVel = 0;
        if(rotateLeft != rotateRight) {
            if(rotateLeft) {
                angularVel = 0.5f;
                //setPos(x - (int)(0.25f*delta), y);
            }else{
                angularVel = -0.5f;
                //setPos(x + (int)(0.25f*delta), y);
            }
        }
        body.setAngularVelocity(angularVel);
        body.setLinearVelocity(new Vec2(xVel, yVel));

        Vec2 position = body.getPosition().mul(Match.PIXELS_PER_METER);
        setPos((int)position.x, (int)position.y);
        setRot((int)Math.toDegrees(body.getAngle()));
    }

    @Override
    public void updateClient(float delta) {
        while(Keyboard.next()){
            if(Keyboard.getEventKey() == Keyboard.KEY_W){
                if(Keyboard.getEventKeyState()){
                    Client.writeMessage("key_pressed;key_w");
                }else{
                    Client.writeMessage("key_released;key_w");
                }
            }else if(Keyboard.getEventKey() == Keyboard.KEY_S){
                if(Keyboard.getEventKeyState()){
                    Client.writeMessage("key_pressed;key_s");
                }else{
                    Client.writeMessage("key_released;key_s");
                }
            }else if(Keyboard.getEventKey() == Keyboard.KEY_A){
                if(Keyboard.getEventKeyState()){
                    Client.writeMessage("key_pressed;key_a");
                }else{
                    Client.writeMessage("key_released;key_a");
                }
            }else if(Keyboard.getEventKey() == Keyboard.KEY_D){
                if(Keyboard.getEventKeyState()){
                    Client.writeMessage("key_pressed;key_d");
                }else{
                    Client.writeMessage("key_released;key_d");
                }
            }else if(Keyboard.getEventKey() == Keyboard.KEY_SPACE){
                if(Keyboard.getEventKeyState()) {
                    Client.writeMessage("fire_em;all");
                }
            }
        }
    }

    public void pressed(String key) {
        switch (key) {
            case "key_w":
                moveForward = true;
                break;
            case "key_s":
                moveBackward = true;
                break;
            case "key_a":
                rotateLeft = true;
                break;
            case "key_d":
                rotateRight = true;
                break;
        }
    }

    public void released(String key) {
        switch (key) {
            case "key_w":
                moveForward = false;
                break;
            case "key_s":
                moveBackward = false;
                break;
            case "key_a":
                rotateLeft = false;
                break;
            case "key_d":
                rotateRight = false;
                break;
        }
    }
}
