package net.tankers.entity;

import io.netty.channel.Channel;
import net.tankers.client.Client;
import net.tankers.server.Server;
import net.tankers.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by idrol on 14-04-2016.
 */
public abstract class NetworkedEntity extends Entity {
    protected int instanceID;
    protected String objectIdentifier;
    protected static int instances = 0;
    protected Server server = null;
    protected Client client = null;
    protected boolean isServer;
    protected Channel channel;


    public NetworkedEntity(Client client, Integer instanceID) {
        objectIdentifier = this.getClass().getSimpleName();
        this.instanceID = instanceID;
        isServer = false;
    }

    public NetworkedEntity(Server server, Channel channel){
        objectIdentifier = this.getClass().getSimpleName();
        this.instanceID = instances;
        instances++;
        isServer = true;
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setServer(Server server){
        this.server = server;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setNetworkState(boolean isServer){
        this.isServer = isServer;
    }

    public int getInstanceID() {
        return instanceID;
    }

    public abstract void decodeData(String[] data);

    public abstract String[] encodeData(String variable);

    public void update(float delta) {
        if(isServer){
            updateServer(delta);
        }else{
            updateClient(delta);
        }
    }

    public List<String> sync(){
        List<String> msg = new ArrayList<String>();
        msg.add(NetworkUtils.encodeBase(this, NetworkUtils.CREATE));
        msg = sync(msg);
        return msg;
    }

    public List<String> remove() {
        List<String> msg = new ArrayList<String>();
        msg.add(NetworkUtils.encodeBase(this, NetworkUtils.DELETE));
        return msg;
    }

    public abstract List<String> sync(List<String> msg);

    public abstract void updateServer(float delta);

    public abstract void updateClient(float delta);
}
