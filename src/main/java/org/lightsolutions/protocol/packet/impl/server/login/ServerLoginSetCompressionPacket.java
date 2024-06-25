package org.lightsolutions.protocol.packet.impl.server.login;

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
@Packet.PacketInfo(connectionState = ConnectionState.LOGIN,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerLoginSetCompressionPacket extends Packet {

    {
        this.setPacketId((byte) 0x03);
    }

    private int threshold;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.threshold = in.readVarInt();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarInt(this.threshold);
    }
}
