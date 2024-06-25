package org.lightsolutions.protocol.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.lightsolutions.protocol.data.Position;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Packet.PacketInfo(connectionState = ConnectionState.PLAY,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerPlayRespawnPacket extends Packet {

    {
        this.setPacketId((byte) 0x45);
    }

    private String dimensionType,dimensionName;
    private long hashedSeed;
    private byte gameMode,previousGameMode;
    private boolean debug,flat;
    private String deathDimensionName;
    private Position deathLocation;
    private int portalCooldown;
    private byte dataKept;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.dimensionType = in.readStringFromBuffer(Short.MAX_VALUE);
        this.dimensionName = in.readStringFromBuffer(Short.MAX_VALUE);

        this.hashedSeed = in.readLong();
        this.gameMode = (byte) in.readUnsignedByte();
        this.previousGameMode = in.readByte();

        this.debug = in.readBoolean();
        this.flat = in.readBoolean();

        if(in.readBoolean()){
            this.deathDimensionName = in.readStringFromBuffer(Short.MAX_VALUE);
            this.deathLocation = in.readPosition();
        }

        this.portalCooldown = in.readVarInt();
        this.dataKept = in.readByte();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeString(this.dimensionType);
        out.writeString(this.dimensionName);

        out.writeLong(this.hashedSeed);
        out.writeByte(this.gameMode);
        out.writeByte(this.previousGameMode);

        out.writeBoolean(this.debug);
        out.writeBoolean(this.flat);

        if (this.deathDimensionName != null && this.deathLocation != null) {
            out.writeBoolean(true);
            out.writeString(this.deathDimensionName);
            out.writePosition(this.deathLocation);
        } else {
            out.writeBoolean(false);
        }

        out.writeVarInt(this.portalCooldown);
        out.writeByte(this.dataKept);
    }
}
