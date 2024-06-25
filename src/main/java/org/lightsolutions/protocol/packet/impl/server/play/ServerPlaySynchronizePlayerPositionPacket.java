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
public class ServerPlaySynchronizePlayerPositionPacket extends Packet {

    {
        this.setPacketId((byte) 0x3E);
    }

    private double posX,posY,posZ;
    private float yaw,pitch;
    private byte flags;
    private int teleportId;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.posX = in.readDouble();
        this.posY = in.readDouble();
        this.posZ = in.readDouble();
        this.yaw = in.readFloat();
        this.pitch = in.readFloat();
        this.flags = in.readByte();
        this.teleportId = in.readVarInt();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeDouble(this.posX);
        out.writeDouble(this.posY);
        out.writeDouble(this.posZ);
        out.writeFloat(this.yaw);
        out.writeFloat(this.pitch);
        out.writeByte(this.flags);
        out.writeVarInt(this.teleportId);
    }
}
