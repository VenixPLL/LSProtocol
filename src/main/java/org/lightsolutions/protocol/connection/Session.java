package org.lightsolutions.protocol.connection;

import com.velocitypowered.natives.util.Natives;
import io.netty.channel.*;
import lombok.Getter;
import org.lightsolutions.LightProtocol;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.ProtocolAttributes;
import org.lightsolutions.protocol.netty.codec.compress.NettyLegacyCompressionCodec;
import org.lightsolutions.protocol.netty.codec.compress.velocity.NettyNativeCompressionDecoder;
import org.lightsolutions.protocol.netty.codec.compress.velocity.NettyNativeCompressionEncoder;
import org.lightsolutions.protocol.netty.codec.frame.NettyFrameDecoder;
import org.lightsolutions.protocol.netty.codec.frame.NettyFrameEncoder;
import org.lightsolutions.protocol.netty.codec.monitor.NettyBandwidthMonitor;
import org.lightsolutions.protocol.netty.codec.monitor.NettyPacketMonitor;
import org.lightsolutions.protocol.netty.codec.packet.NettyPacketBundleCodec;
import org.lightsolutions.protocol.netty.codec.packet.NettyPacketCodec;
import org.lightsolutions.protocol.packet.Packet;

@Getter
public class Session extends SimpleChannelInboundHandler<Packet> {

    private final Channel channel;

    public Session(Channel channel, boolean server){
        this.channel = channel;

        this.preparePipeline();
        this.channel.attr(ProtocolAttributes.packetDirectionKey).set(server ? PacketDirection.SERVERBOUND : PacketDirection.CLIENTBOUND);
        this.channel.attr(ProtocolAttributes.connectionStateKey).set(ConnectionState.HANDSHAKE);
        this.channel.attr(ProtocolAttributes.compressionThresholdKey).set(-1); // -1 Stands for disabled.

        var viaInstance = LightProtocol.getInstance().getViaImplementation();
        if(viaInstance != null){
            viaInstance.setupSession(this);
        }
    }

    public void enableBandwidthMonitor(boolean state){
        var pipeline = this.channel.pipeline();

        if(state) {
            if (pipeline.get(ProtocolAttributes.bandwidthMonitorName) == null) {
                pipeline.addBefore("packetCodec",ProtocolAttributes.bandwidthMonitorName,new NettyBandwidthMonitor());
            }
            return;
        }

        if(pipeline.get(ProtocolAttributes.bandwidthMonitorName) != null)
            pipeline.remove(ProtocolAttributes.bandwidthMonitorName);

    }

    public void enablePacketMonitor(boolean state){
        var pipeline = this.channel.pipeline();

        if(state) {
            if (pipeline.get(ProtocolAttributes.packetMonitorName) == null) {
                pipeline.addBefore("packetCodec",ProtocolAttributes.packetMonitorName,new NettyPacketMonitor());
            }
            return;
        }

        if(pipeline.get(ProtocolAttributes.packetMonitorName) != null)
            pipeline.remove(ProtocolAttributes.packetMonitorName);

    }

    public NettyPacketMonitor getPacketMonitor(){
        return (NettyPacketMonitor) this.channel.pipeline().get(ProtocolAttributes.packetMonitorName);
    }

    public NettyBandwidthMonitor getBandwidthMonitor(){
        return (NettyBandwidthMonitor) this.channel.pipeline().get(ProtocolAttributes.bandwidthMonitorName);
    }

    public ChannelId getId(){
        return this.channel.id();
    }

    public boolean isServer(){
        return this.channel.attr(ProtocolAttributes.packetDirectionKey).get() == PacketDirection.SERVERBOUND;
    }

    public void setConnectionState(ConnectionState connectionState){
        this.channel.attr(ProtocolAttributes.connectionStateKey).set(connectionState);
    }

    public ConnectionState getConnectionState(){
        return this.channel.attr(ProtocolAttributes.connectionStateKey).get();
    }

    public void setCompressionThreshold(int threshold){

        var transportType = (LightProtocol.TransportType)LightProtocol.getInstance().getProtocolProperty().getProperty("transport");
        var viaInstance = LightProtocol.getInstance().getViaImplementation();

        final var pipeline = this.channel.pipeline();
        var currentThreshold = this.getCompressionThreshold();
        this.channel.attr(ProtocolAttributes.compressionThresholdKey).set(threshold);

        if(transportType == LightProtocol.TransportType.LEGACY) {
            if (currentThreshold == -1 && threshold > 0) {
                pipeline.addBefore("packetCodec", "compress", new NettyLegacyCompressionCodec());
            } else if (threshold < 0) {
                pipeline.remove("compress");
            }

            if(viaInstance != null){
                viaInstance.delegateCompressionEvent(this);
            }
            return;
        }

        var decoder = (NettyNativeCompressionDecoder) channel.pipeline()
                .get("decompress");
        var encoder =
                (NettyNativeCompressionEncoder) channel.pipeline().get("compress");
        if (decoder != null && encoder != null) {
            decoder.setThreshold(threshold);
            encoder.setThreshold(threshold);
        } else {
            var compressor = Natives.compress.get().create(-1);

            encoder = new  NettyNativeCompressionEncoder(threshold, compressor);
            decoder = new NettyNativeCompressionDecoder(threshold, compressor);

            channel.pipeline().remove("frameEncoder");
            channel.pipeline().addBefore("packetCodec", "decompress", decoder);
            channel.pipeline().addBefore("packetCodec", "compress", encoder);
        }
        if(viaInstance != null){
            viaInstance.delegateCompressionEvent(this);
        }
        //NATIVE

    }

    public int getCompressionThreshold(){
        return this.channel.attr(ProtocolAttributes.compressionThresholdKey).get();
    }

    private void preparePipeline() {
        var pipeline = this.channel.pipeline();
        pipeline.addLast("frameEncoder", new NettyFrameEncoder());
        pipeline.addLast("frameDecoder", new NettyFrameDecoder());
        pipeline.addLast("packetCodec",new NettyPacketCodec());
        pipeline.addLast("bundler",new NettyPacketBundleCodec());
        pipeline.addLast("handler", this);
    }

    public void autoRead(boolean autoRead){
        this.channel.config().setAutoRead(autoRead);
    }

    public void close(){
        this.channel.close();
    }

    public void sendPacket(Packet packet) {
        if(this.channel.isOpen())
            this.channel.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {}
}
