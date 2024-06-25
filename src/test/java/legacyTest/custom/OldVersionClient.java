package legacyTest.custom;

import io.netty.buffer.Unpooled;
import legacyTest.custom.packets.client.handshake.HandshakePacket;
import legacyTest.custom.packets.client.login.ClientLoginStartPacket;
import legacyTest.custom.packets.server.login.ServerLoginSuccessPacket;
import org.lightsolutions.LightProtocol;
import org.lightsolutions.protocol.connection.Connection;
import org.lightsolutions.protocol.connection.client.MinecraftClient;
import org.lightsolutions.protocol.connection.handler.IConnectionHandler;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.netty.pool.NettyGroupBuilder;
import org.lightsolutions.protocol.packet.Packet;
import org.lightsolutions.protocol.packet.impl.RawPacket;

import java.util.concurrent.Future;

public class OldVersionClient{

    public static void main(String[] args) {
        var inst = new OldVersionClient();
        inst.start();
    }

    public OldVersionClient() {
        new LightProtocol();

        LightProtocol.builder()
                //withCustomPackets (1.8.x) 47 protocol packets
                .withCustomPackets("legacyTest.custom.packets")
                .transport(LightProtocol.TransportType.LEGACY)
                .build();
    }

    public void start(){
        var server = new MinecraftClient(NettyGroupBuilder.newBuilder().createPooledGroup(1));
        server.setBaseHandler(getHandler());
        server.connect("127.0.0.1",25565,(future -> {
            assert future.state() == Future.State.SUCCESS : "Client failed to connect";
        }));
    }

    public static IConnectionHandler getHandler(){
        return new IConnectionHandler() {
            @Override
            public void active(Connection connection) {
                System.out.printf("%s Active%n",connection.getId());
                connection.sendPacket(new HandshakePacket(47,"localhost",25565,2));
                connection.sendPacket(new ClientLoginStartPacket("LSProtocolTest"));
                connection.setConnectionState(ConnectionState.LOGIN);
            }

            @Override
            public void packetReceived(Connection connection, Packet packet) {
                if(packet instanceof RawPacket rawPacket){
                    System.out.println("Raw inbound -> " + new String(rawPacket.getData()));
                }
               if(connection.getConnectionState() == ConnectionState.LOGIN) {
                   //Usage of raw packets
                   if(packet.getPacketId() == 0x03 && packet instanceof RawPacket rawPacket) {
                       // Set Compression Threshold (47,1.8.x)
                       // https://wiki.vg/index.php?title=Protocol&oldid=7368#Set_Compression_2
                       var buf = new PacketBuffer(Unpooled.wrappedBuffer(rawPacket.getData())); //Wrap data
                       var threshold = buf.readVarInt(); //Read threshold
                       connection.setCompressionThreshold(threshold);
                       return;
                   }
               }

               if(packet instanceof ServerLoginSuccessPacket){
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
