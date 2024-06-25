package org.lightsolutions.protocol.packet.impl.client.handshake;

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
@Packet.PacketInfo(connectionState = ConnectionState.HANDSHAKE, packetDirection = PacketDirection.SERVERBOUND)
public class ClientHandshakePacket extends Packet {

    {
        this.setPacketId((byte) 0x00);
    }

    private int protocolId;
    private String hostname;
    private int port;
    private byte nextState;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.protocolId = in.readVarInt();
        this.hostname = in.readStringFromBuffer(32767);
        this.port = in.readShort();
        this.nextState = (byte) in.readVarInt();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarInt(this.protocolId);
        out.writeString(this.hostname);
        out.writeShort(this.port);
        out.writeVarInt(this.nextState);
    }
}
