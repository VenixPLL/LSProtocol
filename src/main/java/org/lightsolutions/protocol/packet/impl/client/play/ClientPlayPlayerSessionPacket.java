package org.lightsolutions.protocol.packet.impl.client.play;

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
@Packet.PacketInfo(connectionState = ConnectionState.PLAY,packetDirection = PacketDirection.SERVERBOUND)
public class ClientPlayPlayerSessionPacket extends Packet {

    {
        this.setPacketId((byte) 0x06);
    }

    private UUID sessionId;
    private PlayerSessionKey playerSessionKey;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.sessionId = in.readUuid();

        var expiresAt = in.readLong();
        var pubKey = new byte[in.readVarInt()];
        in.readBytes(pubKey);

        var signature = new byte[in.readVarInt()];
        in.readBytes(signature);

        this.playerSessionKey = new PlayerSessionKey(expiresAt,pubKey,signature);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeUuid(this.sessionId);

        out.writeLong(this.playerSessionKey.expiresAt());
        out.writeVarInt(this.playerSessionKey.pubKey().length);
        out.writeBytes(this.playerSessionKey.pubKey());

        out.writeVarInt(this.playerSessionKey.signature().length);
        out.writeBytes(this.playerSessionKey.signature());
    }

    public record PlayerSessionKey(long expiresAt,byte[] pubKey, byte[] signature){}
}
