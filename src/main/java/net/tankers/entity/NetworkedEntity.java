package net.tankers.entity;

/**
 * Created by idrol on 14-04-2016.
 */
public abstract class NetworkedEntity extends Entity {
    protected int instanceID;
    protected String objectIdentifier;
    protected static int instances = 0;
    protected boolean server;

    public NetworkedEntity(Boolean isServer, Integer instanceID){
        server = isServer;
        objectIdentifier = this.getClass().getSimpleName();
        if(server){
            this.instanceID = instances;
            instances++;
        }else {
            this.instanceID = instanceID;
        }
    }

    public NetworkedEntity() {
        this(true, 0);
    }

    public void setNetworkState(boolean isServer){
        server = isServer;
    }

    public int getInstanceID() {
        return instanceID;
    }

    public abstract void decodeData(String[] data);

    public abstract String[] encodeData(String variable);

    public void update(float delta) {
        if(server){
            updateServer(delta);
        }else{
            updateClient(delta);
        }
    }

    public abstract void updateServer(float delta);

    public abstract void updateClient(float delta);
}
