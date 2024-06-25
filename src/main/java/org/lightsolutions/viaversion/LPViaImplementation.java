package org.lightsolutions.viaversion;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import net.raphimc.vialoader.ViaLoader;
import net.raphimc.vialoader.impl.platform.ViaBackwardsPlatformImpl;
import net.raphimc.vialoader.impl.platform.ViaRewindPlatformImpl;
import net.raphimc.vialoader.netty.VLPipeline;
import net.raphimc.vialoader.netty.ViaCodec;
import org.lightsolutions.LightProtocol;
import org.lightsolutions.protocol.connection.Session;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.ProtocolAttributes;

public class LPViaImplementation {

    private final int baseProtocol;

    public LPViaImplementation(int baseProtocol){
        this.baseProtocol = baseProtocol;
    }

    public void initialize(){
        LightProtocol.getLogger().info("Initializing ViaVersion...");
        ViaLoader.init(null, null, null, null, ViaBackwardsPlatformImpl::new, ViaRewindPlatformImpl::new);
        Via.getManager().getProviders().use(VersionProvider.class, userConnection -> ProtocolVersion.getProtocol(this.baseProtocol));
        LightProtocol.getLogger().info("ViaVersion Initialized!");
    }

    public void delegateCompressionEvent(Session session){
        final var channel = session.getChannel();
        var removed = channel.pipeline().remove(VLPipeline.VIA_CODEC_NAME);
        channel.pipeline().addBefore("packetCodec",VLPipeline.VIA_CODEC_NAME,removed);
    }

    public void setupSession(Session session){
        var direction = session.getChannel().attr(ProtocolAttributes.packetDirectionKey).get();

        var user = new UserConnectionImpl(session.getChannel(), direction == PacketDirection.CLIENTBOUND);
        new ProtocolPipelineImpl(user);

        session.getChannel().attr(ProtocolAttributes.viaConnection).set(user);
        session.getChannel().pipeline().addBefore("packetCodec", VLPipeline.VIA_CODEC_NAME, new ViaCodec(user));
    }

}
