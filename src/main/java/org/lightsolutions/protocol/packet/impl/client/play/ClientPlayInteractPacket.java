package org.lightsolutions.protocol.packet.impl.client.play;

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
@Packet.PacketInfo(connectionState = ConnectionState.PLAY,packetDirection = PacketDirection.SERVERBOUND)
public class ClientPlayInteractPacket extends Packet {

    {
        this.setPacketId((byte) 0x13);
    }

    private int entityId;
    private int type;
    private float targetX,targetY,targetZ;
    private int hand;
    private boolean sneaking;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.entityId = in.readVarInt();
        this.type = in.readVarInt();

        if(this.type == 2){
            this.targetX = in.readFloat();
            this.targetY = in.readFloat();
            this.targetZ = in.readFloat();
        }
        if(type != 1) this.hand = in.readVarInt();
        this.sneaking = in.readBoolean();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarInt(this.entityId);
        out.writeVarInt(this.type);

        if (this.type == 2) {
            out.writeFloat(this.targetX);
            out.writeFloat(this.targetY);
            out.writeFloat(this.targetZ);
        }

        if (this.type != 1) {
            out.writeVarInt(this.hand);
        }

        out.writeBoolean(this.sneaking);
    }
}
