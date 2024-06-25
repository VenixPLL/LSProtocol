package org.lightsolutions.protocol.packet.impl.server.config;

import lombok.Getter;
import lombok.ToString;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

@Getter
@ToString
@Packet.PacketInfo(connectionState = ConnectionState.CONFIG,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerConfigFinishPacket extends Packet {

    {
        this.setPacketId((byte) 0x02);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {

    }

    @Override
    public void write(PacketBuffer out) throws Exception {

    }
}
