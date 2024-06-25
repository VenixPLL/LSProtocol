package legacyTest;

import com.github.steveice10.opennbt.NBTIO;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import org.lightsolutions.LightProtocol;
import org.lightsolutions.protocol.connection.Connection;
import org.lightsolutions.protocol.connection.handler.IConnectionHandler;
import org.lightsolutions.protocol.connection.server.MinecraftServer;
import org.lightsolutions.protocol.data.Position;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.netty.pool.NettyGroupBuilder;
import org.lightsolutions.protocol.packet.Packet;
import org.lightsolutions.protocol.packet.impl.client.config.ClientConfigFinishPacket;
import org.lightsolutions.protocol.packet.impl.client.config.ClientConfigPluginMessagePacket;
import org.lightsolutions.protocol.packet.impl.client.handshake.ClientHandshakePacket;
import org.lightsolutions.protocol.packet.impl.client.login.ClientLoginAcknowledgedPacket;
import org.lightsolutions.protocol.packet.impl.client.login.ClientLoginStartPacket;
import org.lightsolutions.protocol.packet.impl.server.config.ServerConfigFinishPacket;
import org.lightsolutions.protocol.packet.impl.server.config.ServerConfigRegistryDataPacket;
import org.lightsolutions.protocol.packet.impl.server.login.ServerLoginSetCompressionPacket;
import org.lightsolutions.protocol.packet.impl.server.login.ServerLoginSuccessPacket;
import org.lightsolutions.protocol.packet.impl.server.play.ServerPlayGameEventPacket;
import org.lightsolutions.protocol.packet.impl.server.play.ServerPlayLoginPacket;
import org.lightsolutions.protocol.packet.impl.server.play.ServerPlaySetCenterChunkPacket;
import org.lightsolutions.protocol.packet.impl.server.play.ServerPlaySynchronizePlayerPositionPacket;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

public class VelocityCompressServerTest {

    public static void main(String[] args) throws IOException {
        /*
         *  1.20.4 Protocol Version
         *  Creates Empty World Limbo Server
         *  If client gets kicked during logging, compression failed.
         */
        var tag = NBTIO.readTag(VelocityCompressServerTest.class.getResourceAsStream("/config-registry-data.nbt"));
        new LightProtocol()
                .asDefault()
                .transport(LightProtocol.TransportType.NATIVE)
                .build();

        var server = new MinecraftServer(NettyGroupBuilder.newBuilder().createPooledGroup(1));
        server.setBaseHandler(getHandler(tag));

        server.bind(25565,future -> {
            assert future.state() == Future.State.SUCCESS : "Server failed to bind";
        });
    }

    public static IConnectionHandler getHandler(Tag configTag){
        return new IConnectionHandler() {
            @Override
            public void active(Connection connection) {
                System.out.printf("%s Active%n",connection.getId());
            }

            @Override
            public void packetReceived(Connection connection, Packet packet) {
                System.out.printf("%s %s Inbound -> %s(%s) > %s%n",connection.getId(),connection.getConnectionState().name(),packet.getClass().getSimpleName(),packet.getPacketId(),packet.toString());
                switch(packet){
                    case ClientHandshakePacket handshakePacket -> connection.setConnectionState(handshakePacket.getNextState() == 2 ? ConnectionState.LOGIN : ConnectionState.STATUS);

                    case ClientLoginStartPacket loginStartPacket -> {
                        System.out.printf("%s LOGIN Login request -> %s(%s)%n",connection.getId(),loginStartPacket.getUsername(),loginStartPacket.getPlayerUUID());
                        System.out.printf("%s LOGIN Outbound -> Login Success%n",connection.getId());
                        connection.sendPacket(new ServerLoginSetCompressionPacket(256));
                        connection.setCompressionThreshold(256); // SET COMPRESSION VELOCITY
                        connection.sendPacket(new ServerLoginSuccessPacket(loginStartPacket.getPlayerUUID(),loginStartPacket.getUsername(), Set.of()));
                    }

                    case ClientLoginAcknowledgedPacket ignored -> {
                        System.out.printf("%s LOGIN Inbound -> Login ACK (CONFIG)%n",connection.getId());
                        connection.setConnectionState(ConnectionState.CONFIG);
                        System.out.printf("%s CONFIG Outbound -> Config Registry (CompoundTag)%n",connection.getId());
                        connection.sendPacket(new ServerConfigRegistryDataPacket(configTag));
                        System.out.printf("%s CONFIG Outbound -> Config Finish %n",connection.getId());
                        connection.sendPacket(new ServerConfigFinishPacket());
                    }

                    case ClientConfigPluginMessagePacket pluginMessagePacket -> {
                        var brand = pluginMessagePacket.getData().readStringFromBuffer(32767);
                        System.out.printf("%s CONFIG Inbound -> Brand (%s)%n",connection.getId(),brand);
                    }

                    case ClientConfigFinishPacket configFinishPacket -> {
                        connection.setConnectionState(ConnectionState.PLAY);
                        connection.sendPacket(new ServerPlayLoginPacket(
                                0,
                                false,
                                List.of("minecraft:overworld"),
                                20,
                                12,
                                12,
                                false,
                                true,
                                true,
                                "minecraft:overworld",
                                "overworld",
                                0L,
                                (byte) 1,
                                (byte) 1,
                                true,
                                false,
                                "minecraft:overworld",
                                new Position(0,0,0),
                                1));
                        connection.sendPacket(new ServerPlaySynchronizePlayerPositionPacket((double) 0, 500.0, (double) 0, 90F, 30F, (byte) 0,0));
                        connection.sendPacket(new ServerPlayGameEventPacket((short) 13,0f));
                        connection.sendPacket(new ServerPlaySetCenterChunkPacket(0,0));
                    }

                    default -> {}
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
