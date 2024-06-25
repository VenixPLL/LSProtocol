package org.lightsolutions.protocol.packet.impl.server.play;

import com.github.steveice10.opennbt.tag.builtin.Tag;
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
@Packet.PacketInfo(connectionState = ConnectionState.PLAY,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerPlayEntityEffectPacket extends Packet {

    {
        this.setPacketId((byte) 0x72);
    }

    private int entityId,effectId;
    private byte amplifier;
    private int duration;
    private byte flags;
    private Tag factorCodec;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.entityId = in.readVarInt();
        this.effectId = in.readVarInt();
        this.amplifier = in.readByte();
        this.duration = in.readVarInt();
        this.flags = in.readByte();

        if(in.readBoolean()) this.factorCodec = in.readNBTTag();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarInt(this.entityId);
        out.writeVarInt(this.effectId);
        out.writeByte(this.amplifier);
        out.writeVarInt(this.duration);
        out.writeByte(this.flags);

        out.writeBoolean(this.factorCodec != null);
        if (this.factorCodec != null) out.writeNBTTag(this.factorCodec);
    }
}
