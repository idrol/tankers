package net.tankers.map;

import net.tankers.entity.Entity;
import net.tankers.entity.NetworkedEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by idrol on 20-04-2016.
 */
public abstract class Map {
    protected List<Entity> nonSyncedObjects = new ArrayList<>();
    protected List<NetworkedEntity> syncedObjects = new ArrayList<>();

    public abstract void init();

    public void render(){
        syncedObjects.forEach(Entity::render);
        nonSyncedObjects.forEach(Entity::render);
    }

    public List<Entity> getLocalEntites() {
        return this.nonSyncedObjects;
    }

    public List<NetworkedEntity> getRemoteEntites() {
        return this.syncedObjects;
    }

}
