package net.tankers.entity;

import io.netty.channel.Channel;
import net.tankers.client.Client;
import net.tankers.server.Match;
import net.tankers.server.Server;
import net.tankers.utils.NetworkUtils;

import java.util.List;

/**
 * Created by idrol on 16-04-2016.
 */
public class Player extends NetworkedEntity {
    public String username = "";
    private Channel channel;
    public boolean isInMatch = false;
    public Match match;

    public Player(Integer instanceID) {
        super(instanceID);
    }

    public Player(Server server, Channel channel) {
        super(server);
        this.channel = channel;
    }

    public boolean authenticated = false;

    @Override
    public void updateServer(float delta) {

    }

    @Override
    public void updateClient(float delta) {

    }

    @Override
    public void decodeDataClient(String[] data){
        System.out.println("Decoding data sent to Player instance " + data[0]);
        if(data[0].equals("username")){
            username = data[1];
        }else if(data[0].equals("authenticated")){
            if(data[1].equals("1")){
                authenticated = true;
            }else{
                authenticated = false;
            }
        }
    }

    @Override
    public void decodeDataServer(String[] data) {

    }

    @Override
    public String[] encodeData(String variable) {
        String[] data;
        if(variable.equals("username")){
            data = new String[2];
            data[0] = variable;
            data[1] = username;
            return data;
        }
        return null;
    }

    @Override
    public List<String> sync(List<String> msg) {
        msg.add(NetworkUtils.encodeBase(this, NetworkUtils.UPDATE) + "username:"+username);
        msg.add(NetworkUtils.encodeBase(this, NetworkUtils.UPDATE) + "authenticated:"+((authenticated) ? 1 : 0));
        return msg;
    }

    public void write(String msg) {
        if(isServer){
            channel.writeAndFlush(msg + "\r\n");
        }
    }
}
