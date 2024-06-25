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
public class ServerPlaySetHeadRotationPacket extends Packet {

    {
        this.setPacketId((byte) 0x46);
    }

    private int entityId;
    private float yaw;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.entityId = in.readVarInt();
        this.yaw = in.readByte() * 360 / 256f;
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarInt(this.entityId);
        out.writeByte((byte) (this.yaw * 256 / 360));
    }
}
