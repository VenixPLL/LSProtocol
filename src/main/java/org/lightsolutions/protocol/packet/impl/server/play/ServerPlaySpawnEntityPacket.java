package org.lightsolutions.protocol.packet.impl.server.play;

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
@Packet.PacketInfo(connectionState = ConnectionState.PLAY,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerPlaySpawnEntityPacket extends Packet {

    {
        this.setPacketId((byte) 0x01);
    }

    private int entityId;
    private UUID entityUID;
    private int type;
    private double posX,posY,posZ;
    private float pitch,yaw,headYaw;
    private int delta;
    private short velX,velY,velZ;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.entityId = in.readVarInt();
        this.entityUID = in.readUuid();
        this.type = in.readVarInt();

        this.posX = in.readDouble();
        this.posY = in.readDouble();
        this.posZ = in.readDouble();

        this.pitch = in.readByte() * 360 / 256f;
        this.yaw = in.readByte() * 360 / 256f;
        this.headYaw = in.readByte() * 360 / 256f;;

        this.delta = in.readVarInt();

        this.velX = in.readShort();
        this.velY = in.readShort();
        this.velZ = in.readShort();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarInt(this.entityId);
        out.writeUuid(this.entityUID);
        out.writeVarInt(this.type);

        out.writeDouble(this.posX);
        out.writeDouble(this.posY);
        out.writeDouble(this.posZ);

        out.writeByte((byte) (this.pitch * 256 / 360));
        out.writeByte((byte) (this.yaw * 256 / 360));
        out.writeByte((byte) (this.headYaw * 256 / 360));

        out.writeVarInt(this.delta);

        out.writeShort(this.velX);
        out.writeShort(this.velY);
        out.writeShort(this.velZ);
    }
}
