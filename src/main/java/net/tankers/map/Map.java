package net.tankers.map;

import net.tankers.entity.Entity;
import net.tankers.entity.NetworkedEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by idrol on 20-04-2016.
 */
public abstract class Map {
    private List<Entity> nonSyncedObjects = new ArrayList<>();
    private List<NetworkedEntity> syncedObjects = new ArrayList<>();



}
