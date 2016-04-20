package net.tankers.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.tankers.entity.NetworkedEntity;
import net.tankers.entity.Player;
import net.tankers.entity.Tank;
import net.tankers.main.Game;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by idrol on 13-04-2016.
 */
public class Client {

    private final String host;
    private final int port;

    private Channel channel;
    private EventLoopGroup group = null;
    private Map<String, HashMap<Integer, NetworkedEntity>> entities = new HashMap<String, HashMap<Integer, NetworkedEntity>>();
    public Game game;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        registerNetworkedEntityClass(Tank.class);
        registerNetworkedEntityClass(Player.class);
    }

    public void run() {
        group = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ClientInitializer(this));
            this.channel = bootstrap.connect(host, port).sync().channel();
            System.out.println("got to end");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void loginUser(String username, String password) {
    	writeMessage("login;"+username+":"+password);
    }
    
    public void registerUser(String username, String password, String verifyPassword) {
    	System.out.println("Sent registration stuff");
    	writeMessage("register;"+username+":"+password+":"+verifyPassword);
    }
    
    public void setGame(Game game) {
    	this.game = game;
    }

    public void registerNetworkedEntityClass(Class<?> entityClass){
        if(!NetworkedEntity.class.isAssignableFrom(entityClass)){
            throw new UnsupportedOperationException("Tried to add " + entityClass.getCanonicalName() + " to registry");
        }
        entities.put(entityClass.getName(), new HashMap<Integer, NetworkedEntity>());
    }

    public void decode(String msg){
        String msgType = msg.split(";")[0];
        if(msgType.equals("object")){
            decodeObject(msg.split(";")[1]);
        }else if(msgType.equals("user_info")){

        }
    }

    public void decodeObject(String msg) {
        String[] data = msg.split(":");
        HashMap<Integer, NetworkedEntity> objects = entities.get(data[0]);
        if(data[2].equals("create")){
            if(objects.get(Integer.parseInt(data[1])) == null){
                try {
                    Class<?> clazz = Class.forName(data[0]);
                    Constructor<?> ctor = clazz.getConstructor(Client.class, Integer.class);
                    Object object = ctor.newInstance(this, Integer.parseInt(data[1]));
                    if(object instanceof NetworkedEntity){
                        NetworkedEntity entity = (NetworkedEntity) object;
                        int instanceId = entity.getInstanceID();
                        entity.setClient(this);
                        objects.put(instanceId, entity);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }else if(data[2].equals("update")){
            String[] data2 = new String[data.length-3];
            System.arraycopy(data, 3, data2, 0, data.length - 3);
            objects.get(Integer.parseInt(data[1])).decodeData(data2);
        }else if(data[2].equals("delete")){
            objects.remove(Integer.parseInt(data[1]));
        }
    }

    public void stop() {
        group.shutdownGracefully();
    }

    public void writeMessage(String message) {
        channel.writeAndFlush(message + "\r\n");
    }

}
