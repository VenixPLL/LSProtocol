package org.lightsolutions.protocol.packet.impl.server.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.kyori.adventure.text.Component;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Packet.PacketInfo(connectionState = ConnectionState.CONFIG,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerConfigDisconnectPacket extends Packet {

    {
        this.setPacketId((byte) 0x01);
    }

    private Component reason;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.reason = in.readComponent();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeComponent(this.reason);
    }
}
