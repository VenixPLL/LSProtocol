package org.lightsolutions.protocol.packet.impl.server.play;

import com.github.steveice10.opennbt.tag.builtin.Tag;
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
public class ServerPlayBlockEntityDataPacket extends Packet {

    {
        this.setPacketId((byte) 0x07);
    }

    private Position position;
    private int type;
    private Tag nbtData;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.position = in.readPosition();
        this.type = in.readVarInt();
        this.nbtData = in.readNBTTag();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writePosition(this.position);
        out.writeVarInt(this.type);
        out.writeNBTTag(this.nbtData);
    }
}
