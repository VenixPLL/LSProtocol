package org.lightsolutions.protocol.netty.codec.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.lightsolutions.protocol.packet.Packet;
import org.lightsolutions.protocol.packet.impl.server.play.ServerPlayBundlePacket;

import java.util.List;

public class NettyPacketBundleCodec extends MessageToMessageCodec<Packet,Packet> {

    private ServerPlayBundlePacket bundlePacket;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, List<Object> list) throws Exception {
        if(packet instanceof ServerPlayBundlePacket bundle){
            list.add(new ServerPlayBundlePacket());
            list.addAll(bundle.getPackets());
            list.add(new ServerPlayBundlePacket());
            return;
        }

        list.add(packet);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, Packet packet, List<Object> list) throws Exception {
        if(packet instanceof ServerPlayBundlePacket bundle) {
            if(this.bundlePacket != null) {
                list.add(this.bundlePacket);
                this.bundlePacket = null;
                return;
            }

            this.bundlePacket = bundle;
            return;
        }

        if(this.bundlePacket != null){
            bundlePacket.getPackets().add(packet);
            return;
        }

        list.add(packet);
    }
}
