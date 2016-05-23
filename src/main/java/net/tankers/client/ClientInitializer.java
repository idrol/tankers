package net.tankers.client;

import de.lessvoid.nifty.Nifty;
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
public class ClientInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext sslCtx;
    
    public ClientInitializer(SslContext sslCtx) {
        super();
        this.sslCtx = sslCtx;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        
        pipeline.addLast(sslCtx.newHandler(ch.alloc(), "localhost", 25565));
        
        pipeline.addLast("framer" , new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encoder", new StringEncoder());

        pipeline.addLast("handler", new ClientHandler());
    }
}
