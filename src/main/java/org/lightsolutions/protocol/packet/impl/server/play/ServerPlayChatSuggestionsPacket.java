package org.lightsolutions.protocol.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Packet.PacketInfo(connectionState = ConnectionState.PLAY,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerPlayChatSuggestionsPacket extends Packet {

    {
        this.setPacketId((byte) 0x17);
    }

    private int action;
    private List<String> entries;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.action = in.readVarInt();
        var count = in.readVarInt();
        this.entries = new ArrayList<>(count);
        for(int i=0;i<count;i++) this.entries.add(in.readStringFromBuffer(32767));
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarInt(this.action);
        out.writeVarInt(this.entries.size());
        for(var entry : this.entries) out.writeString(entry);
    }
}
