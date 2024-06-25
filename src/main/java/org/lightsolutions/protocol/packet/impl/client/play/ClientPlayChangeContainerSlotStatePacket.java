package org.lightsolutions.protocol.packet.impl.client.play;

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
@Packet.PacketInfo(connectionState = ConnectionState.PLAY,packetDirection = PacketDirection.SERVERBOUND)
public class ClientPlayChangeContainerSlotStatePacket extends Packet {

    {
        this.setPacketId((byte) 0x0F);
    }

    private int slotId,windowId;
    private boolean state;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.slotId = in.readVarInt();
        this.windowId = in.readVarInt();
        this.state = in.readBoolean();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarInt(this.slotId);
        out.writeVarLong(this.windowId);
        out.writeBoolean(this.state);
    }
}
