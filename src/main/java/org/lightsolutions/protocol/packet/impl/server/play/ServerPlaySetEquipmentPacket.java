package org.lightsolutions.protocol.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.lightsolutions.protocol.data.item.ItemStack;
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
@Packet.PacketInfo(connectionState = ConnectionState.PLAY,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerPlaySetEquipmentPacket extends Packet {

    {
        this.setPacketId((byte) 0x59);
    }

    private int entityId;
    private Map<Integer, ItemStack> equipment;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.entityId = in.readVarInt();

        var hasNextEntry = true;
        this.equipment = new HashMap<>();
        while (hasNextEntry) {
            var rawSlot = in.readByte();
            var slot = rawSlot & 127;
            equipment.put(slot,in.readItemStack());
            hasNextEntry = (rawSlot & 128) == 128;
        }
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarInt(entityId);

        var index = 0;
        for (Map.Entry<Integer, ItemStack> entry : equipment.entrySet()) {
            var rawSlot = entry.getKey();
            var itemStack = entry.getValue();
            if (index != equipment.size() - 1) {
                rawSlot = rawSlot | 128;
            }
            out.writeByte(rawSlot);
            out.writeItemStack(itemStack);
            index++;
        }
    }
}
