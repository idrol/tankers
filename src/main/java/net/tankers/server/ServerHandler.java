package net.tankers.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import net.tankers.entity.NetworkedEntity;
import net.tankers.entity.Player;
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
        Player player = new Player(server);
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
        if(msg.contains(";")){
            message_name = msg.split(";")[0];
        }else{
            if(!msg.contains(":")){
                message_name = msg;
            }else{
                System.err.println("A client tried to send an invalid message " + msg);
            }
        }
        if(channelAuthenticated(ctx.channel())){

        }else{
            if(message_name.equals("login")){
                String[] msgData = msg.split(";")[1].split(":");
                String username = msgData[0];
                String password = msgData[1];
                boolean auth = true;// auth
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
            }else{
                System.out.println("Unauthenticated channel tried message: " + msg);
            }
        }
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
