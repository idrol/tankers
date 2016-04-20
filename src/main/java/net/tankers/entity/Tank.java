package net.tankers.entity;

import io.netty.channel.Channel;
import net.tankers.client.Client;
import net.tankers.server.Server;

import java.util.List;

/**
 * Created by idrol on 15-04-2016.
 */
public class Tank extends NetworkedEntity {

    public Tank(Client client, Integer instanceID) {
        super(client, instanceID);
    }

    public Tank(Server server) {
        super(server);
    }

    @Override
    public void decodeData(String[] data) {

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

    }
}
