package org.lightsolutions.protocol.packet.impl.server.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Packet.PacketInfo(connectionState = ConnectionState.LOGIN,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerLoginEncryptionRequestPacket extends Packet {

    {
        this.setPacketId((byte) 0x01);
    }

    private String serverId;
    private byte[] publicKey;
    private byte[] verifyToken;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.serverId = in.readStringFromBuffer(20);
        this.publicKey = in.readByteArray();
        this.verifyToken = in.readByteArray();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeString(this.serverId);
        out.writeByteArray(this.publicKey);
        out.writeByteArray(this.verifyToken);
    }
}
