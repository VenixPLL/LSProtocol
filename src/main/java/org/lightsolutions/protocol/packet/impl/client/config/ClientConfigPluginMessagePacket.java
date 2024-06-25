package org.lightsolutions.protocol.packet.impl.client.config;

import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Packet.PacketInfo(connectionState = ConnectionState.CONFIG, packetDirection = PacketDirection.SERVERBOUND)
public class ClientConfigPluginMessagePacket extends Packet {

    {
        this.setPacketId((byte) 0x01);
    }

    private String channel;
    private PacketBuffer data;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.channel = in.readStringFromBuffer(32767);
        var bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        this.data = new PacketBuffer(Unpooled.wrappedBuffer(bytes));
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeString(this.channel);
        try {
            out.writeBytes(this.data);
        }finally {
            this.data.release();
        }
    }
}
