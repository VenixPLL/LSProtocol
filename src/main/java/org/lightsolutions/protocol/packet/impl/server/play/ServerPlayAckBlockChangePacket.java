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
public class ServerPlayAckBlockChangePacket extends Packet {

    {
        this.setPacketId((byte) 0x05);
    }

    private int sequenceId;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.sequenceId = in.readVarInt();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarInt(this.sequenceId);
    }
}
