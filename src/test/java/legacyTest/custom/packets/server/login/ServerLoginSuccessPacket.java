package legacyTest.custom.packets.server.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

import java.util.UUID;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Packet.PacketInfo(packetDirection = PacketDirection.CLIENTBOUND, connectionState = ConnectionState.LOGIN)
public class ServerLoginSuccessPacket extends Packet {

    private UUID uuid;
    private String username;

    {
        this.setPacketId((byte) 0x02);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeString(this.uuid.toString());
        out.writeString(this.username);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.uuid = UUID.fromString(in.readStringFromBuffer(86));
        this.username = in.readStringFromBuffer(32);
    }
}
