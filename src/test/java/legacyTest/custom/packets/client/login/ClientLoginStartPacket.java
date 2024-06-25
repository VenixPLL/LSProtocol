package legacyTest.custom.packets.client.login;

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
@Packet.PacketInfo(packetDirection = PacketDirection.SERVERBOUND, connectionState = ConnectionState.LOGIN)
public class ClientLoginStartPacket extends Packet {

    private String username;

    {
        this.setPacketId((byte) 0x00);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeString(this.username);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.username = in.readStringFromBuffer(32);
    }
}