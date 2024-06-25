package org.lightsolutions.protocol.netty.codec.monitor;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Monitoring bandwidth adds additional overhead.
 */
public class NettyBandwidthMonitor extends MessageToMessageCodec<ByteBuf,ByteBuf> {

    private final AtomicLong bytesReceived = new AtomicLong();
    private final AtomicLong bytesSent = new AtomicLong();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        this.bytesSent.set(bytesSent.get() + byteBuf.readableBytes());
        list.add(Unpooled.copiedBuffer(byteBuf));
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        this.bytesReceived.set(bytesReceived.get() + byteBuf.readableBytes());
        list.add(Unpooled.copiedBuffer(byteBuf));
    }

    /**
     * Get total bytes received
     * @return bytes received
     */
    public long getBytesReceived() {
        return this.bytesReceived.get();
    }

    /**
     * Get total bytes sent
     * @return bytes sent
     */
    public long getBytesSent() {
        return this.bytesSent.get();
    }

    /**
     * Reset counter
     */
    public void reset(){
        this.bytesReceived.set(0);
        this.bytesSent.set(0);
    }
}
