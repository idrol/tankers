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

    protected void sendMessages(Channel channel, List<String> msgs) {
        for(String msg: msgs){
            channel.write(msg + NetworkUtils.ENDING);
        }
        channel.flush();
    }

    private boolean channelAuthenticated(Channel channel) {
        return players.get(channel).authenticated;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Player player = players.get(ctx.channel());
        if(player != null && player.isInMatch){
            player.match.receivedMessage(msg, player);
        }else {
            String message_name = "";

            try {
                message_name = NetworkUtils.constructValidMessage(msg)[0];
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
                } else if(message_name.equals("timeplayed")) {
                    try {
                        analyticsHandler.insertSessionTime(msg.split(";")[1].split(":")[0]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                } else if(message_name.equals("matchesplayed")) {
                    try {
                        analyticsHandler.insertSessionPlayedMatches(msg.split(";")[1].split(":")[0]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Unauthenticated channel tried message: " + msg);
                }
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
            sendMessages(ctx.channel(), player.sync());
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
        String messageToSend = null;
        
        if(credentialsStatus.equals("success")) {
        	userHandler.createNewUser(username, password);
        	messageToSend = "Successfully registered user " + username;
        } else if (credentialsStatus.equals("passwordsnotmatching")) {
        	messageToSend = "The passwords do not match";
        } else if (credentialsStatus.equals("duplicateuser")) {
        	messageToSend = "A user with that name already exists";
        } else if (credentialsStatus.equals("tooshortuserpass")) {
        	messageToSend = "Too short username or password, need to be 4 chars minimum";
        }
        
        player.write("notification;" + messageToSend);
    }
    
    private void handleMatchFound() {
    	Player player1 = PlayerQueueHandler.pollPlayer();
    	Player player2 = PlayerQueueHandler.pollPlayer();
        player1.isInMatch = true;
        player2.isInMatch = true;
        Match match = new Match(player1, player2, server);
        player1.match = match;
        player2.match = match;
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

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Player player = players.get(ctx.channel());
        if(player.isInMatch){
            System.out.println("player " + player.username + " was in match notifying of forfeit.");
            Player winner = player.match.getOtherPlayer(player);
            player.match.endGame(winner, player, Match.FORFEIT);
        }else{
            PlayerQueueHandler.removePlayer(player);
            System.out.println("Removed player " + player.username + " from match-queue, channel '" + ctx.channel() + "'");
        }
        ctx.fireChannelInactive();
    }
}
