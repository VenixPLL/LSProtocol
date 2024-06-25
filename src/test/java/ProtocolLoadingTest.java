import org.junit.After;
import org.junit.Test;
import org.lightsolutions.LightProtocol;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.packet.Packet;
import org.lightsolutions.protocol.packet.impl.client.handshake.ClientHandshakePacket;

public class ProtocolLoadingTest {

    @After
    public void cleanup(){
        LightProtocol.getInstance().getPacketRegistry().invalidate();
    }

    @Test
    public void onCreate(){
        new LightProtocol();
        var protocol = LightProtocol.builder().asDefault().build();
        Packet result = null;
        assert (result = protocol.getPacketRegistry().getPacket(ConnectionState.HANDSHAKE, PacketDirection.SERVERBOUND, (byte) 0)) instanceof ClientHandshakePacket : "Packets improperly loaded!";
        LightProtocol.getLogger().info("Result " + result.toString());
    }

}
