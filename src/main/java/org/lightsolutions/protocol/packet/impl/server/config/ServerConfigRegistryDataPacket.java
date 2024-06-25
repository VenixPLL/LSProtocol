package org.lightsolutions.protocol.packet.impl.server.config;

import com.github.steveice10.opennbt.tag.builtin.Tag;
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
@Packet.PacketInfo(connectionState = ConnectionState.CONFIG,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerConfigRegistryDataPacket extends Packet {

    {
        this.setPacketId((byte) 0x05);
    }


    @ToString.Exclude
    private Tag registryData;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.registryData = in.readAnyTag();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeAnyTag(this.registryData);
    }
}
