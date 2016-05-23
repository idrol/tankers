package net.tankers.entity;

import io.netty.channel.Channel;
import net.tankers.client.Client;
import net.tankers.server.Server;
import net.tankers.utils.NetworkUtils;
import org.lwjgl.input.Keyboard;

import java.util.List;

/**
 * Created by idrol on 15-04-2016.
 */
public class Tank extends NetworkedEntity {

    private Player player;

    public Tank(Client client, Integer instanceID) {
        super(instanceID);
    }

    public Tank(Server server, Player player) {
        super(server);
        this.player = player;
    }


    @Override
    public void decodeDataClient(String[] data) {

    }

    @Override
    public void decodeDataServer(String[] data) {

    }

    @Override
    public String[] encodeData(String variable) {
        return new String[0];
    }

    @Override
    public List<String> sync(List<String> msg) {
        return msg;
    }

    @Override
    public void updateServer(float delta) {

    }

    @Override
    public void updateClient(float delta) {
        if(Keyboard.isKeyDown(Keyboard.KEY_W)){
            if(!Keyboard.isKeyDown(Keyboard.KEY_S)){
                Client.writeMessage(NetworkUtils.encodeBase(this, NetworkUtils.UPDATE) + "key_pressed:key_w");
            }
        }else{
            if(Keyboard.isKeyDown(Keyboard.KEY_S)){
                Client.writeMessage(NetworkUtils.encodeBase(this, NetworkUtils.UPDATE) + "key_pressed:key_s");
            }
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_A)){
            if(!Keyboard.isKeyDown(Keyboard.KEY_D)){
                Client.writeMessage(NetworkUtils.encodeBase(this, NetworkUtils.UPDATE) + "key_pressed:key_a");
            }
        }else{
            if(Keyboard.isKeyDown(Keyboard.KEY_D)){
                Client.writeMessage(NetworkUtils.encodeBase(this, NetworkUtils.UPDATE) + "key_pressed:key_d");
            }
        }
    }
}
