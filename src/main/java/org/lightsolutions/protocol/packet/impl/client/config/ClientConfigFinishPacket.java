package org.lightsolutions.protocol.packet.impl.client.config;

import lombok.Getter;
import lombok.ToString;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

@Getter
@ToString
@SuppressWarnings("unused")
@Packet.PacketInfo(connectionState = ConnectionState.CONFIG, packetDirection = PacketDirection.SERVERBOUND)
public class ClientConfigFinishPacket extends Packet {

    {
        this.setPacketId((byte) 0x02);
    }

    /*
        Packet without fields
        https://wiki.vg/Protocol#Finish_Configuration_2
     */

    @Override
    public void read(PacketBuffer in) throws Exception {

    }

    @Override
    public void write(PacketBuffer out) throws Exception {

    }
}
