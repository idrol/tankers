package net.tankers.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import net.tankers.entity.NetworkedEntity;
import net.tankers.entity.Player;
import net.tankers.exceptions.DuplicateUserException;
import net.tankers.exceptions.InvalidClientMsgException;
import net.tankers.server.sqlite.SQLiteJDBC;
import net.tankers.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by idrol on 13-04-2016.
 */
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private List<NetworkedEntity> entities = new ArrayList<NetworkedEntity>();
    private Map<Channel, Player> players = new HashMap<Channel, Player>();
    private SQLiteJDBC sqlite = new SQLiteJDBC();
    private Server server;

    public ServerHandler(Server server) {
        this.server = server;
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
                	break;
            }
            
        }else{
        	System.out.println("Message name: " + message_name);
            if(message_name.equals("login")){
            	System.out.println("Performing login");
                performLogin(msg, ctx);
                
            } else if(message_name.equals("register")) {
            	try {
            		System.out.println("Performing registration");
            		performRegistration(msg, ctx); 
            	} catch (ArrayIndexOutOfBoundsException e) {
            		//In case username or password(s) are blank
            		ctx.writeAndFlush("registernotification;Please fill in all the fields" + NetworkUtils.ENDING);
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
    	String[] msgData = msg.split(";")[1].split(":");
        String username = msgData[0];
        String password = msgData[1];
        System.out.println("Trying to authenticate " + username);
        boolean auth = authenticateUser(username, password);
        System.out.println("AUTH: "+auth);
        Player player = players.get(ctx.channel());
        
        if(auth){
            ctx.channel().writeAndFlush("login_status;1"+ NetworkUtils.ENDING);
            player.authenticated = auth;
            player.username = username;
            broadCast(player.sync());
            ctx.channel().writeAndFlush("user_info;0:0:"+channels.size()+ NetworkUtils.ENDING);
        }else{
            ctx.channel().writeAndFlush("login_status;0"+ NetworkUtils.ENDING);
        }
    }
    
    private boolean authenticateUser(String username, String password) {
    	return sqlite.validateUser(username, password);
    }
    
    private void performRegistration(String msg, ChannelHandlerContext ctx) {
    	String[] msgData = msg.split(";")[1].split(":");
        String username = msgData[0];
        String password = msgData[1];
        String verifyPassword = msgData[2];
        
        Player player = players.get(ctx.channel());
        String credentialsStatus = verifyRegistrationCredentials(username,password,verifyPassword);
        player.write("registernotification;" + credentialsStatus);
        
        System.out.println("Registration notification: " + credentialsStatus);
        
        if(credentialsStatus.equals("success")) {
        	createNewUser(username, password);
        }
    }
    
    private String verifyRegistrationCredentials(String username, String password, String verifyPassword) {
    	if(username.length() >= 4 || password.length() >= 4) {
    		if(!sqlite.isDuplicateUser(username)) {
        		if(password.equals(verifyPassword)) {
        			return "Success";
        		} else {
        			return "Passwords do not match";
        		}
        	} else {
        		return "A user with that name already exists";
        	}
    	} else {
    		return "Too short username or password, need to be 4 chars minimum";
    	}
    }
    
    private void createNewUser(String username, String password) {
    	try {
			sqlite.createUser(username, password);
		} catch (DuplicateUserException e) {
			e.printStackTrace();
		}
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
