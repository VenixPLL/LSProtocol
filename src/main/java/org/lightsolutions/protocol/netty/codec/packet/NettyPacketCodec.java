package org.lightsolutions.protocol.netty.codec.packet;

import com.google.common.base.Throwables;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.MessageToMessageCodec;
import org.lightsolutions.LightProtocol;
import org.lightsolutions.protocol.netty.ProtocolAttributes;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;
import org.lightsolutions.protocol.packet.impl.RawPacket;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class NettyPacketCodec extends MessageToMessageCodec<ByteBuf, Packet> {

    /**
     * previous packet using for debugging errors
     */
    private Packet prevPacket;

    private final boolean forceRaw;
    private final boolean readRaw;

    public NettyPacketCodec(){
        this.forceRaw = (boolean) LightProtocol.getInstance().getProtocolProperty().getProperty("force-raw");
        this.readRaw = (boolean) LightProtocol.getInstance().getProtocolProperty().getProperty("read-raw");
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, List<Object> out) throws Exception {
        var buffer = new PacketBuffer(Unpooled.buffer());
        buffer.writeVarInt(packet.getPacketId());

        try {
            packet.write(buffer);
        } catch(Exception e) {
            LightProtocol.getLogger().warning("Failed to encode packet" + packet.getClass().getSimpleName() + " (" + packet.getPacketId() + ")");
            LightProtocol.getLogger().severe(Throwables.getStackTraceAsString(e));
            return;
        }

        out.add(buffer);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> in) throws Exception {
        var packetDirection = ctx.channel().attr(ProtocolAttributes.packetDirectionKey).get();
        assert packetDirection != null;

        var connectionState = ctx.channel().attr(ProtocolAttributes.connectionStateKey).get();
        assert connectionState != null;

        try {
            var buffer = new PacketBuffer(byteBuf);

            var packetId = buffer.readByte();

            Packet packet = null;
            if(!this.forceRaw) packet = LightProtocol.getInstance().getPacketRegistry().getPacket(connectionState, packetDirection, packetId);

            // Read packet as raw because we don't have that packet implemented in the protocol.
            if (Objects.isNull(packet)) {
                if(this.readRaw) {
                    packet = new RawPacket();
                    packet.setPacketId(packetId);
                    packet.read(buffer);
                    this.prevPacket = packet;
                    in.add(packet);
                }else{ // Read raw was set to false, discard.
                    buffer.discardReadBytes();
                    buffer.clear();
                }
                return;
            }

            try {
                packet.read(buffer);
            } catch (Exception e) {
                LightProtocol.getLogger().warning("Failed to decode packet " + packet.getClass().getSimpleName() + " (" + packet.getPacketId() + ")");
                LightProtocol.getLogger().severe(Throwables.getStackTraceAsString(e));
            }

            if (buffer.readableBytes() > 0) {
                var bytes = new byte[buffer.readableBytes()];
                buffer.readBytes(bytes);
                LightProtocol.getLogger().warning("Previous packet: " + prevPacket);
                LightProtocol.getLogger().warning("Current packet: " + packet);
                throw new DecoderException("Packet too big ID: " + packet + " > " + packetId + "(0x" + Integer.toHexString(packetId) + ") size " + bytes.length + "\nRemaining bytes: " + Arrays.toString(bytes));
            }

            this.prevPacket = packet;
            in.add(packet);
        }catch(Exception e){
            LightProtocol.getLogger().severe(Throwables.getStackTraceAsString(e));
        }
    }
}
