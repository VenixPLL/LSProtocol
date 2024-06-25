package org.lightsolutions.protocol.packet.impl.client.login;

import lombok.Getter;
import lombok.ToString;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

@Getter
@ToString
@Packet.PacketInfo(connectionState = ConnectionState.LOGIN,packetDirection = PacketDirection.SERVERBOUND)
public class ClientLoginAcknowledgedPacket extends Packet {

    /*
     * This packet switches the connection state to configuration.
     * https://wiki.vg/Protocol#Login_Acknowledged
     */

    {
        this.setPacketId((byte) 0x03);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {

    }

    @Override
    public void write(PacketBuffer out) throws Exception {

    }
}
