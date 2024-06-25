package org.lightsolutions.protocol.netty.codec.monitor;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Monitoring packet counts add additional overhead!
 */
public class NettyPacketMonitor extends MessageToMessageCodec<ByteBuf,ByteBuf> {

    private final AtomicLong packetsReceived = new AtomicLong(0);
    private final AtomicLong packetsSent = new AtomicLong(0);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        this.packetsSent.incrementAndGet();
        list.add(Unpooled.copiedBuffer(byteBuf));
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        this.packetsReceived.incrementAndGet();
        list.add(Unpooled.copiedBuffer(byteBuf));
    }

    /**
     * Get amount of packets received
     * @return packets received
     */
    public long getPacketsReceived() {
        return packetsReceived.get();
    }

    /**
     * Get amount of packets sent
     * @return packets sent
     */
    public long getPacketsSent() {
        return packetsSent.get();
    }

    /**
     * Reset packet counter
     */
    public void reset(){
        this.packetsReceived.set(0);
        this.packetsSent.set(0);
    }
}
