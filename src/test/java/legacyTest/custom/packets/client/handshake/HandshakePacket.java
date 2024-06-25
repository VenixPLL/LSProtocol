package legacyTest.custom.packets.client.handshake;

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
@Packet.PacketInfo(packetDirection = PacketDirection.SERVERBOUND, connectionState = ConnectionState.HANDSHAKE)
public class HandshakePacket extends Packet {
    private int protocolId;
    private String host;
    private int port;
    private int nextState;

    {
        this.setPacketId((byte) 0x00);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarInt(this.protocolId);
        out.writeString(this.host);
        out.writeShort(this.port);
        out.writeVarInt(this.nextState);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.protocolId = in.readVarInt();
        this.host = in.readStringFromBuffer(128);
        this.port = in.readShort();
        this.nextState = in.readVarInt();
    }
}
