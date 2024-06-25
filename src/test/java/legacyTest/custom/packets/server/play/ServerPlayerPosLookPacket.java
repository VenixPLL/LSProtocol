package legacyTest.custom.packets.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.lightsolutions.protocol.data.Position;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Packet.PacketInfo(packetDirection = PacketDirection.CLIENTBOUND, connectionState = ConnectionState.PLAY)
public class ServerPlayerPosLookPacket extends Packet {

    private Position pos;
    private float yaw;
    private float pitch;
    private boolean onGround;

    {
        this.setPacketId((byte) 0x08);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeDouble(this.pos.posX());
        out.writeDouble(this.pos.posY());
        out.writeDouble(this.pos.posZ());
        out.writeFloat(this.yaw);
        out.writeFloat(this.pitch);
        out.writeByte((byte) (this.onGround ? 1 : 0));
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        final double x = in.readDouble();
        final double y = in.readDouble();
        final double z = in.readDouble();
        this.pos = new Position(x, y, z);
        this.yaw = in.readFloat();
        this.pitch = in.readFloat();
        this.onGround = in.readByte() == 1;
    }
}
