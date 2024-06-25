package legacyTest.proxy;

import org.lightsolutions.LightProtocol;
import org.lightsolutions.protocol.connection.Connection;
import org.lightsolutions.protocol.connection.client.MinecraftClient;
import org.lightsolutions.protocol.connection.handler.IConnectionHandler;
import org.lightsolutions.protocol.connection.server.MinecraftServer;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.netty.pool.NettyGroupBuilder;
import org.lightsolutions.protocol.netty.pool.types.PoolType;
import org.lightsolutions.protocol.packet.Packet;
import org.lightsolutions.protocol.packet.impl.client.config.ClientConfigFinishPacket;
import org.lightsolutions.protocol.packet.impl.client.handshake.ClientHandshakePacket;
import org.lightsolutions.protocol.packet.impl.client.login.ClientLoginAcknowledgedPacket;
import org.lightsolutions.protocol.packet.impl.server.config.ServerConfigFinishPacket;
import org.lightsolutions.protocol.packet.impl.server.login.ServerLoginSetCompressionPacket;
import org.lightsolutions.protocol.packet.impl.server.login.ServerLoginSuccessPacket;
import org.lightsolutions.protocol.packet.impl.server.play.ServerPlayChunkDataAndUpdateLightPacket;

public class SimpleProxyImplementation {

    public static void main(String[] args) {
        new SimpleProxyImplementation().start();
    }

    public static MinecraftServer server;
    public static MinecraftClient client;

    private static Connection clientConnection;
    private static Connection serverConnection;

    public SimpleProxyImplementation(){
        new LightProtocol();
        LightProtocol.builder()
                /*.withViaVersion(765)*/ // Optional flag for proxy. allows you to join with any version
                .transport(LightProtocol.TransportType.NATIVE)
                .build();

        final var loopGroup = NettyGroupBuilder.newBuilder().createPooledGroup(PoolType.NIO,2);
        server = new MinecraftServer(loopGroup);
        client = new MinecraftClient(loopGroup);
    }

    private static void initClient(){
        client.setBaseHandler(new SessionListener());
        client.connect("127.0.0.1",25565,future -> System.out.printf("Client status %s%n",future.state().name()));
    }

    private static void initServer(){
        server.setBaseHandler(new SessionListener());
        server.bind(25566,future -> System.out.printf("Server status %s%n",future.state().name()));
    }

    public void start(){
        initServer();
    }

    public static class SessionListener implements IConnectionHandler {

        @Override
        public void active(Connection connection) {
            System.out.printf("%s Active%n",connection.getId());
            if(connection.isServer()) {
                serverConnection = connection;
                connection.autoRead(false);
                initClient();
                return;
            }

            clientConnection = connection;
            connection.setConnectionState(ConnectionState.LOGIN);
            serverConnection.autoRead(true);
        }

        @Override
        public void packetReceived(Connection connection, Packet packet) {
            if(connection.isServer()){
                handleServer(connection,packet);
            }else{
                handleClient(connection,packet);
            }
        }

        @Override
        public void inactive(Connection connection) {
            if(connection.isServer()) clientConnection.close();
            serverConnection.close();
            System.out.printf("%s Inactive%n",connection.getId());
        }

        @Override
        public void exception(Connection connection, Throwable throwable) {
            throwable.printStackTrace();

        }

        private void handleServer(Connection connection, Packet packet){
            //System.out.printf("%s > INBOUND > %s%n",connection.getId().asShortText(),packet.toString());
            clientConnection.sendPacket(packet);

            if(packet instanceof ClientHandshakePacket handshakePacket){
                connection.setConnectionState(ConnectionState.LOGIN);
            }else if(packet instanceof ClientLoginAcknowledgedPacket acknowledgedPacket){
                connection.setConnectionState(ConnectionState.CONFIG);
            }else if(packet instanceof ClientConfigFinishPacket finishPacket) {
                connection.setConnectionState(ConnectionState.PLAY);
            }
        }

        private void handleClient(Connection connection, Packet packet) {
            if (!(packet instanceof ServerPlayChunkDataAndUpdateLightPacket)) {
                //System.out.printf("%s > OUTBOUND > %s%n", connection.getId().asShortText(), packet.toString());
                serverConnection.sendPacket(packet);
            }
            if(packet instanceof ServerLoginSetCompressionPacket compressionPacket){
                connection.setCompressionThreshold(compressionPacket.getThreshold());
                serverConnection.setCompressionThreshold(compressionPacket.getThreshold());
            }else if(packet instanceof ServerLoginSuccessPacket){
                connection.setConnectionState(ConnectionState.CONFIG);
            }else if(packet instanceof ServerConfigFinishPacket configFinishPacket){
                connection.setConnectionState(ConnectionState.PLAY);
            }

        }
    }

}
