package net.tankers.client;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by idrol on 13-04-2016.
 */
public class ClientHandler extends SimpleChannelInboundHandler<String> {
    private Client client;

    public ClientHandler(Client client) {
        super();
        this.client = client;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        client.decode(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
