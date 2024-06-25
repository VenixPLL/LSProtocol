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
public class ServerPlayGameEventPacket extends Packet {

    {
        this.setPacketId((byte) 0x20);
    }

    private short eventId;
    private float value;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.eventId = in.readUnsignedByte();
        this.value = in.readFloat();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeByte(this.eventId);
        out.writeFloat(this.value);
    }
}
