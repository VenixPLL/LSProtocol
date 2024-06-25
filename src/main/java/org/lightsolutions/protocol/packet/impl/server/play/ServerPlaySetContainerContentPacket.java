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

import java.util.LinkedList;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Packet.PacketInfo(connectionState = ConnectionState.PLAY,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerPlaySetContainerContentPacket extends Packet {

    {
        this.setPacketId((byte) 0x13);
    }

    private short windowId;
    private int stateId;
    private List<ItemStack> slotData;
    private ItemStack carriedItem;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.windowId = in.readUnsignedByte();
        this.stateId = in.readVarInt();

        var count = in.readVarInt();
        this.slotData = new LinkedList<>();
        for(int i=0;i<count;i++)
            slotData.add(in.readItemStack());

        this.carriedItem = in.readItemStack();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeByte(this.windowId);
        out.writeVarInt(this.stateId);
        out.writeVarInt(this.slotData.size());
        for(var stack : this.slotData) out.writeItemStack(stack);
        out.writeItemStack(this.carriedItem);
    }
}
