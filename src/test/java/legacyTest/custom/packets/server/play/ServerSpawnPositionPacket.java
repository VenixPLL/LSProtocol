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
public class ServerSpawnPositionPacket extends Packet {

    private Position position;

    {
        this.setPacketId((byte) 0x05);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writePosition(this.position);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.position = in.readPosition();
    }
}

