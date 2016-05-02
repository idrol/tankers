package net.tankers.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;

/**
 * Created by idrol on 13-04-2016.
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    private Server server;
    private final SslContext sslCtx;

    public ServerInitializer(Server server, SslContext sslCtx) {
    	this.sslCtx = sslCtx;
        this.server = server;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        
        pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());

        pipeline.addLast(new ServerHandler(server));
    }
}
