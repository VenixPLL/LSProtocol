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
public class ServerPlayBlockActionPacket extends Packet {

    {
        this.setPacketId((byte) 0x08);
    }

    private Position position;
    private byte actionId;
    private byte actionParam;
    private int blockType;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.position = in.readPosition();
        this.actionId = (byte) in.readUnsignedByte();
        this.actionParam = (byte) in.readUnsignedByte();
        this.blockType = in.readVarInt();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writePosition(this.position);
        out.writeByte(this.actionId);
        out.writeByte(this.actionParam);
        out.writeVarInt(this.blockType);
    }
}
