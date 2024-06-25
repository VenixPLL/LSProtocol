package org.lightsolutions.protocol.packet.impl.client.play;

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
@Packet.PacketInfo(connectionState = ConnectionState.PLAY,packetDirection = PacketDirection.SERVERBOUND)
public class ClientPlayUseItemOnPacket extends Packet {

    {
        this.setPacketId((byte) 0x35);
    }

    private int hand;
    private Position position;
    private int face;
    private float cursorX,cursorY,cursorZ;
    private boolean insideBlock;
    private int sequence;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.hand = in.readVarInt();
        this.position = in.readPosition();
        this.face = in.readVarInt();
        this.cursorX = in.readFloat();
        this.cursorY = in.readFloat();
        this.cursorZ = in.readFloat();
        this.insideBlock = in.readBoolean();
        this.sequence = in.readVarInt();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarInt(this.hand);
        out.writePosition(this.position);
        out.writeVarInt(this.face);
        out.writeFloat(this.cursorX);
        out.writeFloat(this.cursorY);
        out.writeFloat(this.cursorZ);
        out.writeBoolean(this.insideBlock);
        out.writeVarInt(this.sequence);
    }
}
