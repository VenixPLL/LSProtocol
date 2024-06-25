package org.lightsolutions.protocol.packet.impl.server.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

import java.util.Objects;
import java.util.UUID;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Packet.PacketInfo(connectionState = ConnectionState.CONFIG,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerConfigRemoveResourcePackPacket extends Packet {

    {
        this.setPacketId((byte) 0x06);
    }

    private UUID uuid;

    @Override
    public void read(PacketBuffer in) throws Exception {
        if(in.readBoolean()) this.uuid = in.readUuid();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        if(Objects.nonNull(this.uuid)){
            out.writeBoolean(true);
            out.writeUuid(this.uuid);
            return;
        }

        out.writeBoolean(false);
    }
}
