package legacyTest;

import org.lightsolutions.LightProtocol;
import org.lightsolutions.protocol.connection.Connection;
import org.lightsolutions.protocol.connection.client.MinecraftClient;
import org.lightsolutions.protocol.connection.handler.IConnectionHandler;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.netty.pool.NettyGroupBuilder;
import org.lightsolutions.protocol.packet.Packet;
import org.lightsolutions.protocol.packet.impl.RawPacket;
import org.lightsolutions.protocol.packet.impl.client.config.ClientConfigFinishPacket;
import org.lightsolutions.protocol.packet.impl.client.handshake.ClientHandshakePacket;
import org.lightsolutions.protocol.packet.impl.client.login.ClientLoginAcknowledgedPacket;
import org.lightsolutions.protocol.packet.impl.client.login.ClientLoginStartPacket;
import org.lightsolutions.protocol.packet.impl.server.config.ServerConfigFinishPacket;
import org.lightsolutions.protocol.packet.impl.server.login.ServerLoginSetCompressionPacket;
import org.lightsolutions.protocol.packet.impl.server.login.ServerLoginSuccessPacket;
import org.lightsolutions.protocol.packet.impl.server.play.ServerPlayChunkDataAndUpdateLightPacket;

import java.util.UUID;
import java.util.concurrent.Future;

public class VelocityCompressClientTest {

    public static void main(String[] args) {
        new LightProtocol()
                .asDefault()
                .transport(LightProtocol.TransportType.NATIVE)
                .withProperty("read-raw",false)
                .build();

        var client = new MinecraftClient(NettyGroupBuilder.newBuilder().createPooledGroup(1));
        client.setBaseHandler(getHandler());

        client.connect("127.0.0.1",25565,future -> {
            assert future.state() == Future.State.SUCCESS : "Client failed to connect";
        });
    }

    private static IConnectionHandler getHandler(){
        return new IConnectionHandler() {
            @Override
            public void active(Connection connection) {
                connection.sendPacket(new ClientHandshakePacket(765,"127.0.0.1",25565, (byte) 2));
                connection.sendPacket(new ClientLoginStartPacket("LSProtocolTest", UUID.randomUUID()));
                connection.setConnectionState(ConnectionState.LOGIN);
            }
            @Override
            public void packetReceived(Connection connection, Packet packet) {
                if(packet instanceof ServerLoginSetCompressionPacket compressionPacket){
                    connection.setCompressionThreshold(compressionPacket.getThreshold());
                }else if(packet instanceof ServerLoginSuccessPacket){
                    connection.sendPacket(new ClientLoginAcknowledgedPacket());
                    connection.setConnectionState(ConnectionState.CONFIG);

                }else if(packet instanceof ServerConfigFinishPacket){
                    connection.sendPacket(new ClientConfigFinishPacket());
                    connection.setConnectionState(ConnectionState.PLAY);
                }
            }
            @Override
            public void inactive(Connection connection) {
                System.out.printf("%s Inactive%n",connection.getId());
            }

            @Override
            public void exception(Connection connection, Throwable throwable) {
                throwable.printStackTrace();
            }
        };
    }

}
