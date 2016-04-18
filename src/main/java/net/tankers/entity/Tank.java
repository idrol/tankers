package net.tankers.entity;

/**
 * Created by idrol on 15-04-2016.
 */
public class Tank extends NetworkedEntity {

    public Tank(Boolean isServer, Integer instanceID) {
        super(isServer, instanceID);
        objectIdentifier = "tank";
    }
}
