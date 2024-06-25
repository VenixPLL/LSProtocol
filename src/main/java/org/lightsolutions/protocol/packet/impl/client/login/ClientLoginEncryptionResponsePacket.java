package org.lightsolutions.protocol.packet.impl.client.login;

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
@Packet.PacketInfo(connectionState = ConnectionState.LOGIN,packetDirection = PacketDirection.SERVERBOUND)
public class ClientLoginEncryptionResponsePacket extends Packet {

    {
        this.setPacketId((byte) 0x01);
    }

    private byte[] sharedSecret;
    private byte[] verifyToken;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.sharedSecret = in.readByteArray();
        this.verifyToken = in.readByteArray();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeByteArray(this.sharedSecret);
        out.writeByteArray(this.verifyToken);
    }
}
