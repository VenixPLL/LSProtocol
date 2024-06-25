package org.lightsolutions.protocol.packet.impl.server.config;

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
public class ServerConfigFeatureFlagsPacket extends Packet {

    {
        this.setPacketId((byte) 0x08);
    }

    private String[] features;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.features = new String[in.readVarInt()];
        for(int i=0;i<this.features.length;i++){
            this.features[i] = in.readString();
        }
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarInt(this.features.length);
        for(var feature : this.features) out.writeString(feature);
    }
}
