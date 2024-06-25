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
public class ServerPlayLookAtPacket extends Packet {

    {
        this.setPacketId((byte) 0x3D);
    }

    private boolean eyes;
    private double targetX,targetY,targetZ;
    private int entityId = -1;
    private boolean entityEyes;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.eyes = in.readVarInt() == 1;
        this.targetX = in.readDouble();
        this.targetY = in.readDouble();
        this.targetZ = in.readDouble();

        if(in.readBoolean()){
            this.entityId = in.readVarInt();
            this.entityEyes = in.readVarInt() == 1;
        }
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarInt(this.eyes ? 1 : 0);
        out.writeDouble(this.targetX);
        out.writeDouble(this.targetY);
        out.writeDouble(this.targetZ);

        if(this.entityId != -1){
            out.writeBoolean(true);
            out.writeVarInt(this.entityId);
            out.writeVarInt(this.entityEyes ? 1 : 0);
        }else{
            out.writeBoolean(false);
        }
    }
}
