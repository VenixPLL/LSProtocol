package org.lightsolutions.protocol.packet.impl.client.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

import java.util.Arrays;
import java.util.BitSet;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Packet.PacketInfo(connectionState = ConnectionState.PLAY,packetDirection = PacketDirection.SERVERBOUND)
public class ClientPlayChatMessagePacket extends Packet {

    {
        this.setPacketId((byte) 0x05);
    }

    private String message;
    private long timestamp,salt;
    private byte[] signature;
    private int messageCount;
    private BitSet acknowledged;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.message = in.readStringFromBuffer(256);
        this.timestamp = in.readLong();
        this.salt = in.readLong();

        if(in.readBoolean()){
            this.signature = new byte[256];
            in.readBytes(this.signature);
        }

        this.messageCount = in.readVarInt();

        var bytes = new byte[3];
        in.readBytes(bytes);
        this.acknowledged = BitSet.valueOf(bytes);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeString(this.message);
        out.writeLong(this.timestamp);
        out.writeLong(this.salt);

        out.writeBoolean(this.signature != null);
        if (this.signature != null) {
            out.writeBytes(this.signature);
        }

        out.writeVarInt(this.messageCount);
        byte[] bytes = this.acknowledged.toByteArray();
        out.writeBytes(Arrays.copyOf(bytes, 3));
    }
}
