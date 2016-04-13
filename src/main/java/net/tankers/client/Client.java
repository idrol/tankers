package net.tankers.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by idrol on 13-04-2016.
 */
public class Client {

    private final String host;
    private final int port;

    private Channel channel;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();

        try{
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientInitializer());

            Channel channel = bootstrap.connect(host, port).sync().channel();
            this.channel = channel;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }

    public void writeMessage(String message) {
        channel.write(message + "\r\n");
        channel.flush();
    }

}
