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
public class ServerPlayPlayerAbilitiesPacket extends Packet {

    {
        this.setPacketId((byte) 0x36);
    }

    private byte flags;
    private float flyingSpeed,fovModifier;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.flags = in.readByte();
        this.flyingSpeed = in.readFloat();
        this.fovModifier = in.readFloat();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeByte(this.flags);
        out.writeFloat(this.flyingSpeed);
        out.writeFloat(this.fovModifier);
    }
}
