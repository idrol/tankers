package net.tankers.client;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.screen.Screen;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import net.tankers.client.analytics.MatchesPlayed;
import net.tankers.entity.*;
import net.tankers.exceptions.NoSuchEntityException;
import net.tankers.main.Game;

import javax.net.ssl.SSLException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by idrol on 13-04-2016.
 */
public class Client {

    private static String host = null;
    private static int port = 25565;
    private static Channel channel;
    
    private static EventLoopGroup group = null;
    private static Map<String, HashMap<Integer, NetworkedEntity>> entities = new HashMap<String, HashMap<Integer, NetworkedEntity>>();
    
    private static Nifty nifty;

    public static String username;
    public static String currentNotification;
    public static Boolean renderResult = false;
    
    public static void init(Nifty nifty) {
    	Client.nifty = nifty;
    	registerNetworkedEntityClass(Tank.class);
        registerNetworkedEntityClass(Player.class);
        registerNetworkedEntityClass(Shell.class);
        registerNetworkedEntityClass(Wall.class);
        registerNetworkedEntityClass(HiddenWall.class);
        registerNetworkedEntityClass(NonCollidable.class);
    }
    
    public static void setHost(String host) {
    	Client.host = host;
    }

    public static void update(float delta) {
        for(HashMap<Integer, NetworkedEntity> map: entities.values()){
            for(NetworkedEntity entity: map.values()){
                entity.updateClient(delta);
            }
        }
    }

    public static void render() {
        for(HashMap<Integer, NetworkedEntity> map: entities.values()){
            for(Entity entity: map.values()){
                entity.render();
            }
        }
    }


    public static void run() {
        group = new NioEventLoopGroup();
        try{
        	final SslContext sslCtx = SslContextBuilder.forClient()
        		.trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ClientInitializer(sslCtx));
            channel = bootstrap.connect(host, port).sync().channel();
        } catch (InterruptedException | SSLException e) {
            e.printStackTrace();
        }
    }

    public static void registerNetworkedEntityClass(Class<?> entityClass){
        if(!NetworkedEntity.class.isAssignableFrom(entityClass)){
            throw new UnsupportedOperationException("Tried to add " + entityClass.getCanonicalName() + " to registry");
        }
        entities.put(entityClass.getName(), new HashMap<Integer, NetworkedEntity>());
    }

    public static void decode(String msg){
        String msgType = msg.split(";")[0];
        
        if(msgType.equals("object")){
            try {
                decodeObject(msg.split(";")[1]);
            } catch (NoSuchEntityException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        } else if (msgType.equals("login_status")) {
        	if(msg.split(";")[1].equals("1")) {
        		nifty.gotoScreen("lobby");
            }
        } else if(msgType.equals("user_info")){
        	
        		
        } else if(msgType.equals("notification")){
        	decodeNotification(msg);
        } else if(msgType.equals("match_found")) {
        	matchFound(msg);
        } else if(msgType.equals("match_result")) {
            matchEnded(msg);
        }
    }

    private static void decodeObject(String msg) throws NoSuchEntityException {
        String[] data = msg.split(":");
        HashMap<Integer, NetworkedEntity> objects = entities.get(data[0]);
        if(objects == null){
            throw new NoSuchEntityException("Entity " + data[0] + " has not been registered on the client.");
        }
        if(data[2].equals("create")){
            if(objects.get(Integer.parseInt(data[1])) == null){
                try {
                    Class<?> clazz = Class.forName(data[0]);
                    Constructor<?> ctor = clazz.getConstructor(Integer.class);
                    System.out.println("Instantiated new remote object");
                    Object object = ctor.newInstance(Integer.parseInt(data[1]));
                    if(object instanceof NetworkedEntity){
                        NetworkedEntity entity = (NetworkedEntity) object;
                        int instanceId = entity.getInstanceID();
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
            NetworkedEntity entity = objects.get(Integer.parseInt(data[1]));
            entity.decodeDataClient(data2);
        }else if(data[2].equals("delete")){
            objects.remove(Integer.parseInt(data[1]));
        }
    }

    public static void loginUser(String username, String password) {
    	System.out.println("Sent login stuff");
    	writeMessage("login;"+username+":"+password);
        Client.username = username;
    }

    public static void registerUser(String username, String password, String verifyPassword) {
    	System.out.println("Sent registration stuff");
    	writeMessage("register;"+username+":"+password+":"+verifyPassword);
    }
    
    private static void decodeNotification(String msg) {
    	String notification = msg.split(";")[1];
    	Screen screen = nifty.getCurrentScreen();
    	Label notificationLabel = screen.findNiftyControl("notification", Label.class);
    	notificationLabel.setText(notification);
    	
    	System.out.println("Notification: "+notification);
    }
    
    private static void matchFound(String msg) {
    	String opponent = msg.split(";")[1];
        nifty.getScreen("lobby").findNiftyControl("gamesearchlabel", Label.class).setText("");
        nifty.getScreen("lobby").findElementById("cancelsearch").setVisible(false);
        System.out.println("Match found against: " + opponent);
        nifty.gotoScreen("game");
    	MatchesPlayed.incrementMatchesPlayed();
    }

    public static void matchEnded(String msg) {
        String arguments = msg.split(";")[1];
        String result = arguments.split(":")[0];
        int reason = Integer.parseInt(arguments.split(":")[1]);
        String textToShow;

        if(result.equals("won")) {
            textToShow = "You won the match";
        } else {
            textToShow = "You lost the match";
        }

        if(reason == 1) {
            textToShow += "!";
        } else {
            textToShow += " due to forfeit";
        }
        System.out.println(msg);
        currentNotification = textToShow;
        renderResult = true;
    }
    
    public static void stop() {
        if(group != null)
            group.shutdownGracefully();
    }

    public static void writeMessage(String message) {
    	if(channel != null)
    		channel.writeAndFlush(message + "\r\n");
    	else
    		System.err.println("Failed to write message to server: Not connected to server");
    }
}
