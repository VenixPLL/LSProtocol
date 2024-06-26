package legacyTest.custom.packets.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Packet.PacketInfo(packetDirection = PacketDirection.CLIENTBOUND, connectionState = ConnectionState.PLAY)
public class ServerPlayerAbilitiesPacket extends Packet {

    private boolean damage, flying, allowFlying, creative;
    private float flySpeed, walkSpeed;

    {
        this.setPacketId((byte) 0x39);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        byte flags = 0;
        if (this.damage) {
            flags = (byte) (flags | 0x1);
        }
        if (this.flying) {
            flags = (byte) (flags | 0x2);
        }
        if (this.allowFlying) {
            flags = (byte) (flags | 0x4);
        }
        if (this.creative) {
            flags = (byte) (flags | 0x8);
        }
        out.writeByte(flags);
        out.writeFloat(this.flySpeed);
        out.writeFloat(this.walkSpeed);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        final byte flags = in.readByte();
        this.damage = ((flags & 0x1) > 0);
        this.flying = ((flags & 0x2) > 0);
        this.allowFlying = ((flags & 0x4) > 0);
        this.creative = ((flags & 0x8) > 0);
        this.flySpeed = in.readFloat();
        this.walkSpeed = in.readFloat();
    }
}
