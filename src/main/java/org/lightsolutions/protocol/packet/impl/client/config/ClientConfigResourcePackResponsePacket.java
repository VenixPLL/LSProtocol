package org.lightsolutions.protocol.packet.impl.client.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

import java.util.UUID;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Packet.PacketInfo(connectionState = ConnectionState.CONFIG, packetDirection = PacketDirection.SERVERBOUND)
public class ClientConfigResourcePackResponsePacket extends Packet {

    {
        this.setPacketId((byte) 0x05);
    }

    private UUID resourcePackUID;
    private int result;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.resourcePackUID = in.readUuid();
        this.result = in.readVarInt();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeUuid(this.resourcePackUID);
        out.writeVarInt(this.result);
    }
}
