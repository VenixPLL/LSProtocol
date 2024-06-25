package org.lightsolutions.protocol.packet.impl.client.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Packet.PacketInfo(connectionState = ConnectionState.PLAY,packetDirection = PacketDirection.SERVERBOUND)
public class ClientPlayChatCommandPacket extends Packet {

    {
        this.setPacketId((byte) 0x04);
    }

    private String command;
    private long timestamp,salt;
    private List<MessageSignature> signatureList;
    private int messageCount;
    private BitSet acknowledged;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.command = in.readStringFromBuffer(256);
        this.timestamp = in.readLong();
        this.salt = in.readLong();

        var count = in.readVarInt();
        this.signatureList = new ArrayList<>(count);
        for(int i=0;i<count;i++){
            var name = in.readStringFromBuffer(16);
            var bytes = new byte[256];
            in.readBytes(bytes);
            this.signatureList.add(new MessageSignature(name,bytes));
        }

        this.messageCount = in.readVarInt();

        var bytes = new byte[3];
        in.readBytes(bytes);
        this.acknowledged = BitSet.valueOf(bytes);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeString(this.command);

        out.writeLong(this.timestamp);
        out.writeLong(this.salt);

        out.writeVarInt(this.signatureList.size());
        for (MessageSignature signature : this.signatureList) {
            out.writeString(signature.name);
            out.writeBytes(signature.signature);
        }

        out.writeVarInt(this.messageCount);

        byte[] bytes = this.acknowledged.toByteArray();
        out.writeBytes(Arrays.copyOf(bytes, 3));
    }
    public record MessageSignature(String name, byte[] signature) {}
}
