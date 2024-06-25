package legacyTest.custom;


import legacyTest.custom.packets.client.handshake.HandshakePacket;
import legacyTest.custom.packets.client.login.ClientLoginStartPacket;
import legacyTest.custom.packets.server.login.ServerLoginSuccessPacket;
import legacyTest.custom.packets.server.play.ServerJoinGamePacket;
import legacyTest.custom.packets.server.play.ServerPlayerAbilitiesPacket;
import legacyTest.custom.packets.server.play.ServerPlayerPosLookPacket;
import legacyTest.custom.packets.server.play.ServerSpawnPositionPacket;
import org.lightsolutions.LightProtocol;
import org.lightsolutions.protocol.connection.Connection;
import org.lightsolutions.protocol.connection.handler.IConnectionHandler;
import org.lightsolutions.protocol.connection.server.MinecraftServer;
import org.lightsolutions.protocol.data.Position;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.netty.pool.NettyGroupBuilder;
import org.lightsolutions.protocol.packet.Packet;

import java.util.UUID;
import java.util.concurrent.Future;

/**
 * Test custom packet implementations
 * Packet implementation from 1.8.x
 * Create empty world server limbo
 */
public class OldVersionServer {

    public static void main(String[] args) {
        var inst = new OldVersionServer();
        inst.start();
    }

    public OldVersionServer() {
        new LightProtocol();

        LightProtocol.builder()
                //withCustomPackets (1.8.x) 47 protocol packets
                .withCustomPackets("legacyTest.custom.packets")
                .transport(LightProtocol.TransportType.LEGACY)
                .build();
    }

    public void start(){
        var server = new MinecraftServer(NettyGroupBuilder.newBuilder().createPooledGroup(1));
        server.setBaseHandler(getHandler());
        server.bind(25565,(future -> {
            assert future.state() == Future.State.SUCCESS : "Server failed to bind";
        }));
    }

    public static IConnectionHandler getHandler(){
        return new IConnectionHandler() {
            @Override
            public void active(Connection connection) {
                System.out.printf("%s Active%n",connection.getId());
            }

            @Override
            public void packetReceived(Connection connection, Packet packet) {
                if(packet instanceof HandshakePacket handshakePacket){
                    connection.setConnectionState(ConnectionState.LOGIN);
                }
                if(packet instanceof ClientLoginStartPacket loginStartPacket){
                    connection.sendPacket(new ServerLoginSuccessPacket(UUID.randomUUID(), loginStartPacket.getUsername()));
                    connection.setConnectionState(ConnectionState.PLAY);
                    connection.sendPacket(new ServerJoinGamePacket(0, (byte) 0, (byte) 0, (byte) 0, 1, "default_1_1", false));
                    connection.sendPacket(new ServerSpawnPositionPacket(new Position(0, 0, 0)));
                    connection.sendPacket(new ServerPlayerAbilitiesPacket(false, true, false, false, 2, 2));
                    connection.sendPacket(new ServerPlayerPosLookPacket(new Position(0, 0, 0), 180, 90, true));
                    // Good old times when you did not have to send entire tag registry just to enter the world.
                    // 4 game packets and you good to go.
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
