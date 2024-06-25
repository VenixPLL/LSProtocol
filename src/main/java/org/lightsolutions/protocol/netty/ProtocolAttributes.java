package org.lightsolutions.protocol.netty;

import com.viaversion.viaversion.api.connection.UserConnection;
import io.netty.util.AttributeKey;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;

public final class ProtocolAttributes {

    public static final String packetMonitorName = "packetMonitor";
    public static final String bandwidthMonitorName = "bandwidth";

    public static final AttributeKey<ConnectionState> connectionStateKey = AttributeKey.valueOf("connectionState");
    public static final AttributeKey<PacketDirection> packetDirectionKey = AttributeKey.valueOf("packetDirection");
    public static final AttributeKey<Integer> compressionThresholdKey = AttributeKey.valueOf("compressionThreshold");

    public static final AttributeKey<UserConnection> viaConnection = AttributeKey.valueOf("viaConnection");


}
