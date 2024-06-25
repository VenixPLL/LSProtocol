package org.lightsolutions.protocol.connection.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import lombok.Getter;
import lombok.Setter;
import org.lightsolutions.protocol.connection.Connection;
import org.lightsolutions.protocol.connection.handler.IConnectionHandler;
import org.lightsolutions.protocol.netty.pool.NettyGroupBuilder;

import java.util.concurrent.Future;
import java.util.function.Consumer;

@Setter
public class MinecraftClient {

    @Getter
    private final Bootstrap clientBootstrap = new Bootstrap();

    /**
     * Initial connection handler, can be changed per connection depending on your code
     * connection.setConnectionHandler(handler)
     */
    @Getter
    private IConnectionHandler baseHandler;

    public MinecraftClient(EventLoopGroup loopGroup){
        this.clientBootstrap.group(loopGroup);
        this.clientBootstrap.channel(NettyGroupBuilder.newBuilder().getClientChannel(loopGroup));
        this.clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                new Connection(socketChannel,false,baseHandler); //Initialize channel, and create new connection object.
            }
        });
    }

    public void connect(String host,int port, Consumer<Future<? super Void>> callback){
        this.clientBootstrap.connect(host,port).syncUninterruptibly().addListener(callback::accept);
    }

}
