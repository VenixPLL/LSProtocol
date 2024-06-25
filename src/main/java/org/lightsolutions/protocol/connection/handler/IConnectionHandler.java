package org.lightsolutions.protocol.connection.handler;

import org.lightsolutions.protocol.connection.Connection;
import org.lightsolutions.protocol.packet.Packet;

public interface IConnectionHandler {

    void active(Connection connection);
    void packetReceived(Connection connection, Packet packet);
    void inactive(Connection connection);

    void exception(Connection connection, Throwable throwable);

}
