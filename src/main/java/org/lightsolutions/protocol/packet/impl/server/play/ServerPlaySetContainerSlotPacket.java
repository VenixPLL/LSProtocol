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

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Packet.PacketInfo(connectionState = ConnectionState.PLAY,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerPlaySetContainerSlotPacket extends Packet {

    {
        this.setPacketId((byte) 0x15);
    }

    private short windowId;
    private int stateId;
    private short slotId;
    private ItemStack slotData;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.windowId = in.readByte();
        this.stateId = in.readVarInt();
        this.slotId = in.readShort();
        this.slotData = in.readItemStack();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeByte(this.windowId);
        out.writeVarInt(this.stateId);
        out.writeShort(this.slotId);
        out.writeItemStack(this.slotData);
    }
}
