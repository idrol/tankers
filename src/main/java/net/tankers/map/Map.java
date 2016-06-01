package net.tankers.map;

import net.tankers.entity.Entity;
import net.tankers.entity.NetworkedEntity;
import net.tankers.entity.NetworkedGameEntity;
import net.tankers.server.Match;
import net.tankers.server.Server;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by idrol on 20-04-2016.
 */
public abstract class Map {
    private List<NetworkedGameEntity> syncedObjects = new ArrayList<>();
    private World world;
    private Set<Body> bodies;

    public Map(World world, Set<Body> bodies){
        this.world = world;
        this.bodies = bodies;
    }

    public void syncWithMatch(Match match) {
        match.broadCast(buildSyncMessages());
    }

    public List<String> buildSyncMessages(){
        List<String> messages = new ArrayList<>();
        for(NetworkedEntity entity: syncedObjects){
            messages.addAll(entity.sync());
        }
        return messages;
    }

    public void addObject(NetworkedGameEntity entity) {
        entity.setup(world, bodies);
        syncedObjects.add(entity);
    }

    public abstract void init(Server server, Match match);

}
