package org.lightsolutions.protocol.packet.impl.server.login;

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
@Packet.PacketInfo(connectionState = ConnectionState.LOGIN,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerLoginPluginRequestPacket extends Packet {

    {
        this.setPacketId((byte) 0x04);
    }

    private int messageId;
    private String channel;
    private PacketBuffer data;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.messageId = in.readVarInt();
        this.channel = in.readStringFromBuffer(32767);
        try {
            this.data = new PacketBuffer(Unpooled.wrappedBuffer(in));
        }finally {
            in.release();
        }
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarInt(this.messageId);

        out.writeString(this.channel);
        try {
            out.writeBytes(this.data);
        }finally {
            this.data.release();
        }
    }
}
