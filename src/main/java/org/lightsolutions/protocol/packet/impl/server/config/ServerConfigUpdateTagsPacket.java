package org.lightsolutions.protocol.packet.impl.server.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Packet.PacketInfo(connectionState = ConnectionState.CONFIG,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerConfigUpdateTagsPacket extends Packet {

    {
        this.setPacketId((byte) 0x09);
    }

    @ToString.Exclude
    private Map<String, Map<String, int[]>> tags;

    @Override
    public void read(PacketBuffer in) throws Exception {
        int totalTagCount = in.readVarInt();
        this.tags = new HashMap<>(totalTagCount,1);
        for (int i = 0; i < totalTagCount; i++) {
            Map<String, int[]> tag = new HashMap<>();
            String tagName = in.readString();
            int tagsCount = in.readVarInt();
            for (int j = 0; j < tagsCount; j++) {
                String name = in.readString();
                int entriesCount = in.readVarInt();
                int[] entries = new int[entriesCount];
                for (int index = 0; index < entriesCount; index++) {
                    entries[index] = in.readVarInt();
                }

                tag.put(name, entries);
            }
            tags.put(tagName, tag);
        }
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarInt(tags.size());
        for (Map.Entry<String, Map<String, int[]>> tagSet : tags.entrySet()) {
            out.writeString(tagSet.getKey());
            out.writeVarInt(tagSet.getValue().size());
            for (Map.Entry<String, int[]> tag : tagSet.getValue().entrySet()) {
                out.writeString(tag.getKey());
                out.writeVarInt(tag.getValue().length);
                for (int id : tag.getValue()) {
                    out.writeVarInt(id);
                }
            }
        }
    }
}
