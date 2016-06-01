package net.tankers.entity;

import net.tankers.server.Match;
import net.tankers.server.Server;

import java.util.List;

/**
 * Created by local-admin on 30-05-2016.
 */
public class HiddenWall extends NetworkedGameEntity{

    public HiddenWall(Integer instanceID) {
        super(instanceID);
    }

    public HiddenWall(Server server, Match match){
        super(server, match);
    }

    @Override
    public void render() {

    }
}
