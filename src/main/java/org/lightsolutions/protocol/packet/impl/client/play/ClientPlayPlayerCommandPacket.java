package org.lightsolutions.protocol.packet.impl.client.play;

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
@Packet.PacketInfo(connectionState = ConnectionState.PLAY,packetDirection = PacketDirection.SERVERBOUND)
public class ClientPlayPlayerCommandPacket extends Packet {

    {
        this.setPacketId((byte) 0x22);
    }

    private int entityId,actionId,jumpBoost;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.entityId = in.readVarInt();
        this.actionId = in.readVarInt();
        this.jumpBoost = in.readVarInt();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarInt(this.entityId);
        out.writeVarInt(this.actionId);
        out.writeVarInt(this.jumpBoost);
    }
}
