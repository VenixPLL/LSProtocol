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
public class ServerPlayUpdateEntityPositionPacket extends Packet {

    {
        this.setPacketId((byte) 0x2C);
    }

    private int entityId;
    private short deltaX,deltaY,deltaZ;
    private boolean onGround;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.entityId = in.readVarInt();
        this.deltaX = in.readShort();
        this.deltaY = in.readShort();
        this.deltaZ = in.readShort();
        this.onGround = in.readBoolean();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarInt(this.entityId);
        out.writeShort(this.deltaX);
        out.writeShort(this.deltaY);
        out.writeShort(this.deltaZ);
        out.writeBoolean(this.onGround);
    }
}
