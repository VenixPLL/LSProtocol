package org.lightsolutions.protocol.packet.impl.server.play;

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
@Packet.PacketInfo(connectionState = ConnectionState.PLAY,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerPlayRemoveEntitiesPacket extends Packet {

    {
        this.setPacketId((byte) 0x40);
    }

    private int[] entityIds;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.entityIds = new int[in.readVarInt()];
        for(int i=0;i<entityIds.length;i++){
            this.entityIds[i] = in.readVarInt();
        }
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarInt(this.entityIds.length);
        for(int i : entityIds) out.writeVarInt(i);
    }
}
