package net.tankers.entity;

import net.tankers.server.EntityUserData;
import net.tankers.server.Match;
import net.tankers.server.Server;
import net.tankers.utils.NetworkUtils;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.*;

import java.util.List;
import java.util.Set;

/**
 * Created by local-admin on 30-05-2016.
 */
public class NetworkedGameEntity extends NetworkedEntity{

    private Match match;

    public NetworkedGameEntity(Integer instanceID) {
        super(instanceID);
    }

    public NetworkedGameEntity(Server server, Match match) {
        super(server);
        this.match = match;
        sizeX = 5;
        sizeY = 10;
    }

    @Override
    public void setup(World world, Set<Body> bodies) {
        BodyDef boxDef = new BodyDef();
        boxDef.position.set(Match.toMeters(x), Match.toMeters(y));
        boxDef.type = BodyType.STATIC;
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(Match.toMeters(sizeX)/2, Match.toMeters(sizeY)/2);
        Body box = world.createBody(boxDef);
        FixtureDef boxFixture = new FixtureDef();
        boxFixture.density = 1f;
        boxFixture.shape = boxShape;
        boxFixture.userData = new EntityUserData(this);
        box.createFixture(boxFixture);
        body = box;
        bodies.add(box);
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

    }

    @Override
    public void updateClient(float delta) {

    }
}
