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
public class ServerPlayTeleportEntityPacket extends Packet {

    {
        this.setPacketId((byte) 0x6D);
    }

    private int entityId;
    private double posX,posY,posZ;
    private float yaw,pitch;
    private boolean onGround;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.entityId = in.readVarInt();

        this.posX = in.readDouble();
        this.posY = in.readDouble();
        this.posZ = in.readDouble();

        this.pitch = in.readByte() * 360 / 256f;
        this.yaw = in.readByte() * 360 / 256f;

        this.onGround = in.readBoolean();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarInt(this.entityId);

        out.writeDouble(this.posX);
        out.writeDouble(this.posY);
        out.writeDouble(this.posZ);

        out.writeByte((byte)(this.pitch * 256 / 360));
        out.writeByte((byte)(this.yaw * 256 / 360));

        out.writeBoolean(this.onGround);
    }
}
