package net.tankers.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import net.tankers.entity.NetworkedEntity;
import net.tankers.entity.Player;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by idrol on 13-04-2016.
 */
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private List<NetworkedEntity> entities = new ArrayList<NetworkedEntity>();

    private static final int CREATE = 1;
    private static final int UPDATE = 2;
    private static final int DELETE = 3;

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        // Once session is secured, send a greeting and register the channel to the global channel
        // list so the channel received the messages from others
        ctx.pipeline();
        try {
            ctx.writeAndFlush(
                    "Welcome to " + InetAddress.getLocalHost().getHostName() + " chat service!\n");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        channels.add(ctx.channel());
        Player player = new Player();
        int instanceID = player.getInstanceID();
        entities.add(new Player());
        broadCast(encodeBase(player, instanceID, CREATE));
    }

    private String encodeBase(NetworkedEntity entity, int instanceID, int action) {
        String base = "";
        base += entity.getClass().getName()+":";
        base += instanceID+":";
        if(action == CREATE){
            base += "create";
        }else if(action == UPDATE) {
            base += "update";
        }else if(action == DELETE) {
            base += "delete";
        }
        return base;
    }

    protected void broadCast(String msg) {
        for(Channel c: channels) {
            c.writeAndFlush(msg);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("Received message from [" + ctx.channel().remoteAddress() + "]: " + msg);
        for (Channel c: channels) {
            if (c != ctx.channel()) {
                c.writeAndFlush("[" + ctx.channel().remoteAddress() + "] " + msg + '\n');
            } else {
                c.writeAndFlush("[you] " + msg + '\n');
            }
        }

        // Close the connection if the client has sent 'bye'.
        if ("bye".equals(msg.toLowerCase())) {
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
