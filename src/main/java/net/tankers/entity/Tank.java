package net.tankers.entity;

/**
 * Created by idrol on 15-04-2016.
 */
public class Tank extends NetworkedEntity {

    public Tank(Boolean isServer, Integer instanceID) {
        super(isServer, instanceID);
        objectIdentifier = "tank";
    }

    @Override
    public void decodeData(String[] data) {

    }

    @Override
    public String[] encodeData(String variable) {
        return new String[0];
    }

    @Override
    public void updateServer(float delta) {

    }

    @Override
    public void updateClient(float delta) {

    }
}
