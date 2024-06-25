package org.lightsolutions.protocol.netty.pool;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.incubator.channel.uring.IOUring;
import io.netty.incubator.channel.uring.IOUringEventLoopGroup;
import io.netty.incubator.channel.uring.IOUringServerSocketChannel;
import io.netty.incubator.channel.uring.IOUringSocketChannel;
import org.lightsolutions.protocol.netty.pool.types.PoolType;

public class NettyGroupBuilder {

    /**
     * Create a new netty group builder object
     * @return group builder.
     */
    public static NettyGroupBuilder newBuilder(){
        return new NettyGroupBuilder();
    }

    /**
     * Create netty loop group
     * @param type ThreadPool type IO_URING,EPOLL,NIO
     * @param threads number of max_threads to use in the pool
     * @return NettyEventLoopGroup
     */
    public EventLoopGroup createPooledGroup(PoolType type, int threads){
        return switch(type){
            case NIO -> new NioEventLoopGroup(threads);
            case EPOLL -> {
                if(!Epoll.isAvailable()) throw new AssertionError("Epoll is not available on this operating system!");
                yield new EpollEventLoopGroup(threads);
            }
            case SO_URING -> {
                if(!IOUring.isAvailable()) throw new AssertionError("IOUring is not available on this operating system!");
                yield new IOUringEventLoopGroup(threads);
            }
        };
    }

    public Class<? extends ServerChannel> getServerChannel(EventLoopGroup eventLoopGroup){
        return switch(eventLoopGroup){
            case EpollEventLoopGroup l -> EpollServerSocketChannel.class;
            case IOUringEventLoopGroup l -> IOUringServerSocketChannel.class;
            case null, default -> NioServerSocketChannel.class;
        };
    }

    public Class<? extends SocketChannel> getClientChannel(EventLoopGroup eventLoopGroup){
        return switch(eventLoopGroup){
            case EpollEventLoopGroup l -> EpollSocketChannel.class;
            case IOUringEventLoopGroup l -> IOUringSocketChannel.class;
            case null, default -> NioSocketChannel.class;
        };
    }

    /**
     * Auto-detect operating system and create pool
     * @param threads num threads for execution pool
     * @return new execution pool
     */
    public EventLoopGroup createPooledGroup(int threads){
        if(!Epoll.isAvailable() && !IOUring.isAvailable()) return createPooledGroup(PoolType.NIO,threads);

        PoolType poolType = PoolType.NIO;
        if(Epoll.isAvailable()) poolType = PoolType.EPOLL;
        if(IOUring.isAvailable()) poolType = PoolType.SO_URING;
        return createPooledGroup(poolType,threads);
    }

}
