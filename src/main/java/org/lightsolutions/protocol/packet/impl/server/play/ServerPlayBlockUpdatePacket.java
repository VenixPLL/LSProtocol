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
public class ServerPlayBlockUpdatePacket extends Packet {

    {
        this.setPacketId((byte) 0x09);
    }

    private Position position;
    private int blockState;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.position = in.readPosition();
        this.blockState = in.readVarInt();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writePosition(this.position);
        out.writeVarInt(this.blockState);
    }
}
