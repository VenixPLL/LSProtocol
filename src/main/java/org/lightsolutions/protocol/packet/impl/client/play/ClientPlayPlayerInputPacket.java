package org.lightsolutions.protocol.packet.impl.client.play;

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
@Packet.PacketInfo(connectionState = ConnectionState.PLAY,packetDirection = PacketDirection.SERVERBOUND)
public class ClientPlayPlayerInputPacket extends Packet {

    {
        this.setPacketId((byte) 0x23);
    }

    private float sideways,forward;
    private short flags;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.sideways = in.readFloat();
        this.forward = in.readFloat();
        this.flags = in.readUnsignedByte();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeFloat(this.sideways);
        out.writeFloat(this.forward);
        out.writeByte(this.flags);
    }
}
