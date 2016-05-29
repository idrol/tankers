package net.tankers.server;

import net.tankers.entity.Entity;

/**
 * Created by local-admin on 29-05-2016.
 */
public class EntityUserData {
    public int sensor_id = Entity.GENERIC_ENTITY_SENSOR;
    public Entity entity;

    public EntityUserData(Entity entity) {
        this.entity = entity;
    }

    @Override
    public String toString(){
        return "User data contains " + sensor_id + ", " + entity + " ";
    }
}
