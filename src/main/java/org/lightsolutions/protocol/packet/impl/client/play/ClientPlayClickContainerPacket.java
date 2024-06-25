package org.lightsolutions.protocol.packet.impl.client.play;

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
@Packet.PacketInfo(connectionState = ConnectionState.PLAY,packetDirection = PacketDirection.SERVERBOUND)
public class ClientPlayClickContainerPacket extends Packet {


    {
        this.setPacketId((byte) 0x0D);
    }

    private short windowId;
    private int stateId;
    private short slot;
    private byte button;
    private int mode;
    private Map<Short, ItemStack> changedSlots;
    private ItemStack carriedItem;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.windowId = in.readUnsignedByte();
        this.stateId = in.readVarInt();
        this.slot = in.readShort();
        this.button = in.readByte();
        this.mode = in.readVarInt();

        var slotsLength = in.readVarInt();
        this.changedSlots = new HashMap<>(slotsLength,1.0F);
        for(int i=0;i<slotsLength;i++)
            this.changedSlots.put(in.readShort(),in.readItemStack());


        this.carriedItem = in.readItemStack();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeByte(this.windowId);
        out.writeVarInt(this.stateId);
        out.writeShort(this.slot);
        out.writeByte(this.button);
        out.writeVarInt(this.mode);

        out.writeVarInt(this.changedSlots.size());
        for (Map.Entry<Short, ItemStack> entry : this.changedSlots.entrySet()) {
            out.writeShort(entry.getKey());
            out.writeItemStack(entry.getValue());
        }

        out.writeItemStack(this.carriedItem);
    }
}
