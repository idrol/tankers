package net.tankers.entity;

import net.tankers.server.EntityUserData;
import net.tankers.server.Match;
import net.tankers.server.Server;
import net.tankers.utils.NetworkUtils;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glPopMatrix;

/**
 * Created by local-admin on 28-05-2016.
 */
public class Shell extends NetworkedEntity {
    private Match match;
    private Player player;
    public boolean shouldDie = false;

    public Shell(Integer instanceID) {
        super(instanceID);
    }

    public Shell(Server server, Match match, Player player) {
        super(server);
        this.match = match;
        this.player = player;
        sizeX = 5;
        sizeY = 10;
    }

    public Player getPlayer() {
        return player;
    }

    public void setup(World world, Set<Body> bodies, int angle, Vec2 vel) {
        BodyDef boxDef = new BodyDef();
        boxDef.position.set(Match.toMeters(x), Match.toMeters(y));
        boxDef.angle = (float)Math.toRadians(angle);
        boxDef.type = BodyType.DYNAMIC;
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(Match.toMeters(sizeX)/2, Match.toMeters(sizeY)/2);
        Body box = world.createBody(boxDef);
        FixtureDef boxFixture = new FixtureDef();
        boxFixture.density = 1f;
        boxFixture.shape = boxShape;
        EntityUserData entityUserData = new EntityUserData(this);
        entityUserData.sensor_id = Entity.SHELL_SENSOR;
        boxFixture.userData = entityUserData;
        box.createFixture(boxFixture);
        body = box;
        body.setLinearVelocity(vel);
        bodies.add(box);
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
    public void destroy() {
        super.destroy();
        match.broadCast(NetworkUtils.encodeBase(this, NetworkUtils.DELETE));
        match.removeShell(this);
    }

    @Override
    public Entity setRot(int z) {
        super.setRot(z);
        match.broadCast(NetworkUtils.encodeBase(this, NetworkUtils.UPDATE) + "rotZ:" + rotZ);
        return this;
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
        Vec2 position = body.getPosition().mul(Match.PIXELS_PER_METER);
        setPos((int)position.x, (int)position.y);
        setRot((int)Math.toDegrees(body.getAngle()));
        if(shouldDie) destroy();
    }

    @Override
    public void updateClient(float delta) {

    }

    @Override
    public void render() {
        Color.white.bind();
        glDisable(GL_TEXTURE_GEN_S);
        glDisable(GL_TEXTURE_GEN_T);
        if(texture == null) {
            try {
                texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("block.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        texture.bind();
        glPushMatrix();
        glTranslatef(x, y, 0);
        glRotatef(rotZ, 0, 0, 1);
            glBegin(GL_QUADS);
                glTexCoord2f(1f,0f);
                glVertex2i(-sizeX/2, -sizeY/2);
                glTexCoord2f(1f,0f);
                glVertex2i(-sizeX/2, sizeY/2);
                glTexCoord2f(1f,1f);
                glVertex2i(sizeX/2, sizeY/2);
                glTexCoord2f(1f,1f);
                glVertex2i(sizeX/2, -sizeY/2);
            glEnd();
        glPopMatrix();
    }
}
