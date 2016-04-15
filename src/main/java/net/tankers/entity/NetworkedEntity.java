package net.tankers.entity;

/**
 * Created by idrol on 14-04-2016.
 */
public class NetworkedEntity {
    protected int instanceID;
    protected String objectIdentifier;
    protected static int instances = 0;

    public NetworkedEntity(){
        objectIdentifier = this.getClass().getSimpleName();
        instanceID = instances;
        instances++;
    }

    public int getInstanceID() {
        return instanceID;
    }

    public void decodeData(String[] data){

    }
}
