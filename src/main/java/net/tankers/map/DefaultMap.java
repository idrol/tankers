package net.tankers.map;

import net.tankers.entity.Entity;

/**
 * Created by Adrian on 22-05-2016.
 */
public class DefaultMap extends Map {

    @Override
    public void init() {
        nonSyncedObjects.add(new Entity().setPos(100, 100).setSize(20, 20).setColor(1, 0, 0));
        nonSyncedObjects.add(new Entity().setPos(400, 300).setSize(20, 20).setColor(1, 0, 0));
    }

}
