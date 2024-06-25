package org.lightsolutions.protocol.packet.impl.client.config;

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
@Packet.PacketInfo(connectionState = ConnectionState.CONFIG, packetDirection = PacketDirection.SERVERBOUND)
public class ClientConfigPongPacket extends Packet {

    {
        this.setPacketId((byte) 0x04);
    }

    private int pingID;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.pingID = in.readInt();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeInt(this.pingID);
    }
}
