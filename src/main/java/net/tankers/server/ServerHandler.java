package net.tankers.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import net.tankers.entity.NetworkedEntity;
import net.tankers.entity.Player;
import net.tankers.exceptions.InvalidClientMsgException;
import net.tankers.server.sqlite.SQLiteJDBC;
import net.tankers.utils.NetworkUtils;

import java.util.*;

/**
 * Created by idrol on 13-04-2016.
 */
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private List<NetworkedEntity> entities = new ArrayList<NetworkedEntity>();
    private Map<Channel, Player> players = new HashMap<Channel, Player>();
    private List<Match> matches = new LinkedList<>();
    private Server server;
    private SQLiteJDBC sqlite = new SQLiteJDBC();
    private AnalyticsHandler analyticsHandler = new AnalyticsHandler(sqlite);
    private UserHandler userHandler = new UserHandler(sqlite);

    public ServerHandler(Server server) {
        this.server = server;
        userHandler.printAllUsers();
    }

    public List<Match> getMatches() {
        return matches;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        // Once session is secured, send a greeting and register the channel to the global channel
        // list so the channel received the messages from others
        ctx.pipeline();


        channels.add(ctx.channel());
        Player player = new Player(server, ctx.channel());
        entities.add(player);
        players.put(ctx.channel(), player);
    }

    protected void broadCast(List<String> msgs){
        for(Channel c: channels) {
            for(String msg: msgs){
                c.write(msg+ NetworkUtils.ENDING);
            }
            c.flush();
        }
    }

    private boolean channelAuthenticated(Channel channel) {
        return players.get(channel).authenticated;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println(msg);
        String message_name = "";
        
        try {
        	message_name = constructValidMessage(msg);
        } catch (InvalidClientMsgException e) {
        	System.err.println("A client tried to send an invalid message " + msg);
        }
        
        if(channelAuthenticated(ctx.channel())){
            switch (message_name){
                case "search_match":
                	System.out.println("Case: search_match");
                	PlayerQueueHandler.addPlayerToQueue(players.get(ctx.channel()));
                	
                	if(PlayerQueueHandler.size()>1) {
                		handleMatchFound();
                	}
                	
                	break;
                case "route":
                    String[] data = msg.split(";")[1].split(":");
                    if(data[0].equals("match")){
                        Match match = matches.get(Integer.parseInt(data[2]));
                        Channel channel = ctx.channel();
                        Player player = players.get(channel);
                        // Make sure that only a player participating in a match exchanges data. Avoids some hacks
                        if(MatchHandler.validateData(player, match)){
                            MatchHandler.handleMessage(match, player, new String(Base64.getDecoder().decode(data[2].getBytes())));
                        }
                    }
                    break;
            }
            
        }else{
        	System.out.println("Message name: " + message_name);
            if(message_name.equals("login")){
                performLogin(msg, ctx);
                
            } else if(message_name.equals("register")) {
            	try {
            		performRegistration(msg, ctx); 
            	} catch (ArrayIndexOutOfBoundsException e) {
            		
            		//In case username or password(s) are blank
            		ctx.writeAndFlush("notification;Please fill in all the fields" + NetworkUtils.ENDING);
            	}
            } else {
            	System.out.println("Unauthenticated channel tried message: " + msg);
            }
        }
    }
    
    private String constructValidMessage(String msg) throws InvalidClientMsgException {
    	if(msg.contains(";")){
            return msg.split(";")[0];
        }else{
            if(!msg.contains(":")){
                return msg;
            }else{
                throw new InvalidClientMsgException();
            }
        }
    }
    
    private void performLogin(String msg, ChannelHandlerContext ctx) {
    	String username, password;
    	
    	String[] msgData = msg.split(";")[1].split(":");
    	
    	try {
    		username = msgData[0];
    		password = msgData[1];
    	} catch (ArrayIndexOutOfBoundsException e) {
    		username = "";
    		password = "";
    		e.printStackTrace();
    	}
    	
        boolean auth = userHandler.authenticateUser(username, password);
        Player player = players.get(ctx.channel());
        
        if(auth){
            ctx.channel().writeAndFlush("login_status;1"+ NetworkUtils.ENDING);
            player.authenticated = auth;
            player.username = username;
            broadCast(player.sync());
            ctx.channel().writeAndFlush("user_info;0:0:"+channels.size()+ NetworkUtils.ENDING);
        }else{
            ctx.channel().writeAndFlush("login_status;0"+ NetworkUtils.ENDING);
            ctx.channel().writeAndFlush("notification;Invalid username or password"+ NetworkUtils.ENDING);
        }
    }
    
    void performRegistration(String msg, ChannelHandlerContext ctx) {
    	String[] msgData = msg.split(";")[1].split(":");
        String username = msgData[0];
        String password = msgData[1];
        String verifyPassword = msgData[2];
        
        Player player = players.get(ctx.channel());
        String credentialsStatus = userHandler.verifyRegistrationCredentials(username,password,verifyPassword);
        player.write("notification;" + credentialsStatus);
        
        System.out.println("Registration notification: " + credentialsStatus);
        
        if(credentialsStatus.equalsIgnoreCase("Success")) {
        	userHandler.createNewUser(username, password);
        }
    }
    
    private void handleMatchFound() {
    	Player player1 = PlayerQueueHandler.pollPlayer();
    	Player player2 = PlayerQueueHandler.pollPlayer();
        Match match = new Match(player1, player2, server);
		matches.add(match);
        match.init();
        match.broadCast("match_id;" + matches.indexOf(match));
        match.start();
		System.out.println("Match Found");
    }
    
    public void shutDownGracefully() {
    	sqlite.closeConnection();
    	System.out.println("Server shut down gracefully");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        channels.remove(ctx.channel());
        Player player = players.get(ctx.channel());
        entities.remove(player);
        players.remove(ctx.channel());
        broadCast(player.remove());
        ctx.close();
    }
}
