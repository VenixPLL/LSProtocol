package org.lightsolutions.protocol.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import org.lightsolutions.protocol.connection.handler.IConnectionHandler;
import org.lightsolutions.protocol.packet.Packet;

import java.util.Objects;

@Getter
public final class Connection extends Session{

    private IConnectionHandler connectionHandler;

    public Connection(Channel channel,boolean server) {
        super(channel,server);
    }

    public Connection(Channel channel, boolean server, IConnectionHandler handler) {
        super(channel,server);
        this.connectionHandler = handler;
    }

    public void setConnectionHandler(IConnectionHandler connectionHandler) {
        this.autoRead(false);
        this.connectionHandler = connectionHandler;
        this.autoRead(true);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if(Objects.nonNull(this.connectionHandler)) this.connectionHandler.active(this);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        if(Objects.nonNull(this.connectionHandler)) this.connectionHandler.packetReceived(this,packet);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(Objects.nonNull(this.connectionHandler)) this.connectionHandler.inactive(this);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(Objects.nonNull(this.connectionHandler)) this.connectionHandler.exception(this,cause);
    }
}
