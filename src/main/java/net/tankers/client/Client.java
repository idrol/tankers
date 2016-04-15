package net.tankers.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.tankers.entity.NetworkedEntity;
import net.tankers.entity.Tank;

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

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        registerNetworkedEntityClass(Tank.class);
    }

    public void run() {
        group = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ClientInitializer());

            Channel channel = bootstrap.connect(host, port).sync().channel();
            this.channel = channel;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void registerNetworkedEntityClass(Class<?> entityClass){
        if(!NetworkedEntity.class.isAssignableFrom(entityClass)){
            throw new UnsupportedOperationException("Tried to add " + entityClass.getCanonicalName() + " to registry");
        }
        entities.put(entityClass.getName(), new HashMap<Integer, NetworkedEntity>());
    }

    public void decode(String msg){
        String[] data = msg.split(":");
        HashMap<Integer, NetworkedEntity> objects = entities.get(data[0]);
        if(data[2].equals("create")){
            if(objects.get(data[1]) == null){
                try {
                    Class<?> clazz = Class.forName(data[0]);
                    Constructor<?> ctor = clazz.getConstructor();
                    Object object = ctor.newInstance(new Object[]{});
                    if(object instanceof NetworkedEntity){
                        int instanceId = ((NetworkedEntity) object).getInstanceID();
                        objects.put(instanceId, (NetworkedEntity) object);
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
            for(int i=3;i<data.length;i++){
                data2[i-3] = data[i];
            }
            objects.get(data[1]).decodeData(data2);
        }else if(data[2].equals("delete")){
            objects.remove(data[1]);
        }
    }

    public void stop() {
        group.shutdownGracefully();
    }

    public void writeMessage(String message) {
        channel.writeAndFlush(message + "\r\n");
    }

}
