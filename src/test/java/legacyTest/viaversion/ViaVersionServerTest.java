package legacyTest.viaversion;

import legacyTest.custom.OldVersionServer;
import org.lightsolutions.LightProtocol;
import org.lightsolutions.protocol.connection.server.MinecraftServer;
import org.lightsolutions.protocol.netty.pool.NettyGroupBuilder;

import java.util.concurrent.Future;

public class ViaVersionServerTest {

    public static void main(String[] args) {
        var inst = new ViaVersionServerTest();
        inst.start();
    }

    public ViaVersionServerTest() {
        new LightProtocol();

        LightProtocol.builder()
                .withCustomPackets("legacyTest.custom.packets") // Load old version 47 (1.8.x)
                .transport(LightProtocol.TransportType.LEGACY)
                .withViaVersion(47) // Enable ViaVersion for base 47 (1.8.x)
                .build();
    }

    public void start(){
        var server = new MinecraftServer(NettyGroupBuilder.newBuilder().createPooledGroup(1));
        server.setBaseHandler(OldVersionServer.getHandler()); // Use handler from OldVersionServer Test
        server.bind(25565,(future -> {
            assert future.state() == Future.State.SUCCESS : "Server failed to bind";
        }));
    }

}
