package legacyTest.viaversion;

import com.github.steveice10.opennbt.NBTIO;
import legacyTest.VelocityCompressServerTest;
import org.lightsolutions.LightProtocol;
import org.lightsolutions.protocol.connection.server.MinecraftServer;
import org.lightsolutions.protocol.netty.pool.NettyGroupBuilder;

import java.io.IOException;
import java.util.concurrent.Future;

public class ViaVersionServerLatestTest {

    public static void main(String[] args) throws IOException {
        var inst = new ViaVersionServerLatestTest();
        inst.start();
    }

    public ViaVersionServerLatestTest() {
        new LightProtocol();

        LightProtocol.builder()
                .withViaVersion(765)
                .transport(LightProtocol.TransportType.NATIVE) // Also test with native transport lol
                .build();
    }

    public void start() throws IOException {
        var tag = NBTIO.readTag(VelocityCompressServerTest.class.getResourceAsStream("/config-registry-data.nbt"));
        var server = new MinecraftServer(NettyGroupBuilder.newBuilder().createPooledGroup(1));
        server.setBaseHandler(VelocityCompressServerTest.getHandler(tag)); // Use handler from VelocityCompressServerTest Test
        server.bind(25565,(future -> {
            assert future.state() == Future.State.SUCCESS : "Server failed to bind";
        }));
    }

}
