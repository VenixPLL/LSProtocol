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

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Packet.PacketInfo(connectionState = ConnectionState.PLAY,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerPlayLoginPacket extends Packet {

    {
        this.setPacketId((byte) 0x29);
    }

    private int entityId;
    private boolean hardcore;
    private List<String> dimensionNames;
    private int maxPlayers,viewDistance,simulationDistance;
    private boolean reducedDebugInfo,enableRespawnScreen,limitedCrafting;
    private String dimensionType;
    private String dimensionName;
    private long hashSeed;
    private byte gameMode,prevGameMode;
    private boolean debug,flat;
    private String deathDimensionName;
    private Position deathLocation;
    private int portalCoolDown;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.entityId = in.readInt();
        this.hardcore = in.readBoolean();

        var dimCount = in.readVarInt();
        this.dimensionNames = new ArrayList<>(dimCount);
        for(int i=0;i<dimCount;i++) this.dimensionNames.add(in.readStringFromBuffer(Short.MAX_VALUE));

        this.maxPlayers = in.readVarInt();
        this.viewDistance = in.readVarInt();
        this.simulationDistance = in.readVarInt();

        this.reducedDebugInfo = in.readBoolean();
        this.enableRespawnScreen = in.readBoolean();
        this.limitedCrafting = in.readBoolean();

        this.dimensionType = in.readStringFromBuffer(Short.MAX_VALUE);
        this.dimensionName = in.readStringFromBuffer(Short.MAX_VALUE);

        this.hashSeed = in.readLong();
        this.gameMode = (byte) in.readUnsignedByte();
        this.prevGameMode = in.readByte();

        this.debug = in.readBoolean();
        this.flat = in.readBoolean();

        if(in.readBoolean()){
            this.deathDimensionName = in.readStringFromBuffer(Short.MAX_VALUE);
            this.deathLocation = in.readPosition();
        }

        this.portalCoolDown = in.readVarInt();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeInt(this.entityId);
        out.writeBoolean(this.hardcore);

        out.writeVarInt(this.dimensionNames.size());
        for (String dimensionName : this.dimensionNames) out.writeString(dimensionName);

        out.writeVarInt(this.maxPlayers);
        out.writeVarInt(this.viewDistance);
        out.writeVarInt(this.simulationDistance);

        out.writeBoolean(this.reducedDebugInfo);
        out.writeBoolean(this.enableRespawnScreen);
        out.writeBoolean(this.limitedCrafting);

        out.writeString(this.dimensionType);
        out.writeString(this.dimensionName);

        out.writeLong(this.hashSeed);
        out.writeByte(this.gameMode);
        out.writeByte(this.prevGameMode);

        out.writeBoolean(this.debug);
        out.writeBoolean(this.flat);

        out.writeBoolean(this.deathDimensionName != null);
        if (this.deathDimensionName != null) {
            out.writeString(this.deathDimensionName);
            out.writePosition(this.deathLocation);
        }

        out.writeVarInt(this.portalCoolDown);
    }
}
