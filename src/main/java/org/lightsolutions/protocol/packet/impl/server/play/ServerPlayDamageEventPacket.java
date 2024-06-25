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
public class ServerPlayDamageEventPacket extends Packet {

    {
        this.setPacketId((byte) 0x19);
    }

    private int entityId;
    private int sourceTypeId;
    private int sourceCauseId;
    private int sourceDirectId;

    private Position optionalPosition;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.entityId = in.readVarInt();
        this.sourceTypeId = in.readVarInt();
        this.sourceCauseId = in.readVarInt();
        this.sourceDirectId = in.readVarInt();
        if(in.readBoolean()) this.optionalPosition = new Position(in.readDouble(), in.readDouble(), in.readDouble());
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarInt(this.entityId);
        out.writeVarInt(this.sourceTypeId);
        out.writeVarInt(this.sourceCauseId);
        out.writeVarInt(this.sourceDirectId);
        if(this.optionalPosition != null){
            out.writeBoolean(true);
            out.writeDouble(this.optionalPosition.posX());
            out.writeDouble(this.optionalPosition.posY());
            out.writeDouble(this.optionalPosition.posZ());
        }else{
            out.writeBoolean(false);
        }
    }
}
