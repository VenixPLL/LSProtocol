package org.lightsolutions.protocol.packet.impl.client.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

import java.util.UUID;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Packet.PacketInfo(connectionState = ConnectionState.LOGIN,packetDirection = PacketDirection.SERVERBOUND)
public class ClientLoginStartPacket extends Packet {

    {
        this.setPacketId((byte) 0x00);
    }

    private String username;
    private UUID playerUUID;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.username = in.readStringFromBuffer(16);
        this.playerUUID = in.readUuid();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeString(this.username);
        out.writeUuid(this.playerUUID);
    }
}
