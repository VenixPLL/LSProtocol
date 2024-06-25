package org.lightsolutions;

import io.netty.util.ResourceLeakDetector;
import lombok.Getter;
import org.lightsolutions.protocol.packet.registry.PacketRegistry;
import org.lightsolutions.protocol.property.ProtocolProperty;
import org.lightsolutions.viaversion.LPViaImplementation;

import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LightProtocol {

    @Getter
    private static final Logger logger = Logger.getGlobal();

    private static LightProtocol INSTANCE;

    @Getter
    private final PacketRegistry packetRegistry = new PacketRegistry();

    @Getter
    private final ProtocolProperty protocolProperty = new ProtocolProperty();

    @Getter
    private LPViaImplementation viaImplementation;

    static {
        var inputStream = LightProtocol.class.getResourceAsStream("/logging.properties");
        try {
            LogManager.getLogManager().readConfiguration(inputStream);
        } catch (final IOException e) {
            Logger.getAnonymousLogger().severe("Could not load default logging.properties file");
            Logger.getAnonymousLogger().severe(e.getMessage());
        }

        System.setProperty("java.awt.headless", "true");
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);

    }

    public LightProtocol(){
        INSTANCE = this;
    }
    public LightProtocol asDefault(){
        this.protocolProperty.setProperty("transport", TransportType.LEGACY);
        this.protocolProperty.setProperty("read-raw",true); // Read packets that are not implemented.
        this.protocolProperty.setProperty("force-raw",false); // Bypass packet registry and read all as RawPacket.
        return this;
    }

    public LightProtocol withCustomPackets(String packagePath){
        this.protocolProperty.setProperty("custom-packets-path",packagePath);
        return this;
    }

    public LightProtocol withProperty(String property, Object value){
        this.protocolProperty.setProperty(property,value);
        return this;
    }

    public LightProtocol withViaVersion(int baseProtocol){
        this.viaImplementation = new LPViaImplementation(baseProtocol);
        this.viaImplementation.initialize();
        return this;
    }

    public LightProtocol transport(TransportType transportType){
        this.protocolProperty.setProperty("transport",transportType);
        return this;
    }

    public LightProtocol build(){
        this.packetRegistry.initializeRegistry();

        var transport = (TransportType)this.protocolProperty.getProperty("transport");
        if(transport == TransportType.NATIVE){
            if(System.getProperty("os.name").contains("win")) throw new UnsupportedOperationException("Native transport is unsupported on Windows!");
            getLogger().info("Using PaperMC Velocity native cipher and compression!");
            return this;
        }

        return this;
    }

    public static LightProtocol builder() {
        INSTANCE.asDefault();
        return INSTANCE;
    }

    public static LightProtocol getInstance() {
        return INSTANCE;
    }

    public enum TransportType {
        LEGACY,NATIVE
    }
}
