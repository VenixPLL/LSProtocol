package org.lightsolutions.protocol.packet.impl.client.status;

import lombok.ToString;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

@ToString
@Packet.PacketInfo(connectionState = ConnectionState.STATUS, packetDirection = PacketDirection.SERVERBOUND)
public class ClientStatusRequestPacket extends Packet{

    {
        this.setPacketId((byte) 0x00);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {

    }

    @Override
    public void write(PacketBuffer out) throws Exception {

    }
}
