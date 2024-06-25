package org.lightsolutions.protocol.packet.impl.server.play;

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
@Packet.PacketInfo(connectionState = ConnectionState.PLAY,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerPlaySetTickingStatePacket extends Packet {

    {
        this.setPacketId((byte) 0x6E);
    }

    private float tickRate;
    private boolean frozen;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.tickRate = in.readFloat();
        this.frozen = in.readBoolean();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeFloat(this.tickRate);
        out.writeBoolean(this.frozen);
    }
}
