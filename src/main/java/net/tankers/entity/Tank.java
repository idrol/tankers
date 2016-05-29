package net.tankers.entity;

import io.netty.channel.Channel;
import net.tankers.client.Client;
import net.tankers.server.EntityUserData;
import net.tankers.server.Match;
import net.tankers.server.Server;
import net.tankers.utils.NetworkUtils;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.List;
import java.util.Set;

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
    public void wasHitByShell(Shell shell) {
        if(shell.getPlayer() != player){
            // Was shot shud die
            match.endGame(shell.getPlayer(), player, Match.WON);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        match.broadCast(NetworkUtils.encodeBase(this, NetworkUtils.DELETE));
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
    public void setup(World world, Set<Body> bodies) {
        BodyDef boxDef = new BodyDef();
        boxDef.position.set(Match.toMeters(x), Match.toMeters(y));
        boxDef.type = BodyType.DYNAMIC;
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(Match.toMeters(sizeX)/2, Match.toMeters(sizeY)/2);
        Body box = world.createBody(boxDef);
        FixtureDef boxFixture = new FixtureDef();
        boxFixture.density = 1f;
        boxFixture.shape = boxShape;
        EntityUserData entityUserData = new EntityUserData(this);
        entityUserData.sensor_id = Entity.TANK_SENSOR;
        boxFixture.userData = entityUserData;
        box.createFixture(boxFixture);
        body = box;
        bodies.add(box);
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
                xVel = (float)(speed * Math.sin(body.getAngle()));
                yVel = -(float)(speed * Math.cos(body.getAngle()));
            }else {
                xVel = -(float)(speed * Math.sin(body.getAngle()));
                yVel = (float)(speed * Math.cos(body.getAngle()));
            }
        }
        float angularVel = 0;
        if(rotateLeft != rotateRight) {
            if(rotateLeft) {
                angularVel = 0.5f;
            }else{
                angularVel = -0.5f;
            }
        }
        body.setAngularVelocity(angularVel);
        body.setLinearVelocity(new Vec2(xVel, yVel));

        Vec2 position = body.getPosition().mul(Match.PIXELS_PER_METER);
        setPos((int)position.x, (int)position.y);
        setRot((int)Math.toDegrees(body.getAngle()));
    }

    public void fire() {
        float length = 50;
        float speed = 0.75f;
        Vec2 pos = new Vec2(Match.toMeters(x), Match.toMeters(y));
        Vec2 vel = new Vec2((float)(speed * Math.sin(body.getAngle())), -(float)(speed * Math.cos(body.getAngle())));
        pos.add(vel).add(vel);
        Shell shell = new Shell(server, match, player);
        shell.x = x + (int)(length * Math.sin(body.getAngle()));
        shell.y = y - (int)(length * Math.cos(body.getAngle()));
        System.out.println("Tank is at " + x + ", " + y);
        System.out.println("Spawning shell at " + shell.x + ", " + shell.y);
        shell.setup(match.getWorld(), match.getBodies(), rotZ, vel);
        match.addShell(shell);
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
                    Client.writeMessage("key_pressed;fire");
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
            case "fire":
                fire();
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
