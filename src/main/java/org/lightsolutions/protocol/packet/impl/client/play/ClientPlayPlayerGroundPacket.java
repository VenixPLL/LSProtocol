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
public class ClientPlayPlayerGroundPacket extends Packet {

    {
        this.setPacketId((byte) 0x1A);
    }

    private boolean onGround;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.onGround = in.readBoolean();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeBoolean(this.onGround);
    }
}
