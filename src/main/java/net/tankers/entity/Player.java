package net.tankers.entity;

/**
 * Created by idrol on 16-04-2016.
 */
public class Player extends NetworkedEntity {
    protected String username = "";

    public Player(Boolean isServer, Integer instanceID) {
        super(isServer, instanceID);
    }

    public Player() {
        super();
    }

    @Override
    public void updateServer(float delta) {

    }

    @Override
    public void updateClient(float delta) {

    }

    @Override
    public void decodeData(String[] data){
        if(data[0].equals("username")){
            username = data[1];
        }
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
}
