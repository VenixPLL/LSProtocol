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
public class ClientPlaySetHeldItemPacket extends Packet {

    {
        this.setPacketId((byte) 0x2C);
    }

    private short slot;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.slot = in.readShort();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeShort(this.slot);
    }
}
