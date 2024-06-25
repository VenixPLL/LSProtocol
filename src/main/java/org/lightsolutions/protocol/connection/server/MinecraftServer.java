package org.lightsolutions.protocol.connection.server;

import io.netty.bootstrap.ServerBootstrap;
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

@Getter
@Setter
public class MinecraftServer {

    private final ServerBootstrap serverBootstrap = new ServerBootstrap();

    /**
     * Initial connection handler, can be changed per connection depending on your code
     * connection.setConnectionHandler(handler)
     */
    private IConnectionHandler baseHandler;

    public MinecraftServer(EventLoopGroup loopGroup){
        this.serverBootstrap.group(loopGroup);
        this.serverBootstrap.channel(NettyGroupBuilder.newBuilder().getServerChannel(loopGroup));
        this.serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
               new Connection(socketChannel,true,baseHandler); //Initialize channel, and create new connection object.
            }
        });
    }

    public void bind(int bindPort, Consumer<Future<? super Void>> callback){
        this.serverBootstrap.bind(bindPort).syncUninterruptibly().addListener(callback::accept);
    }


}
