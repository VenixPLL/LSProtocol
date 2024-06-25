package org.lightsolutions.protocol.packet.impl.server.play;

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
public class ServerPlayEntityEventPacket extends Packet {

    {
        this.setPacketId((byte) 0x1D);
    }

    private int entityId;
    private byte entityStatus;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.entityId = in.readInt();
        this.entityStatus = in.readByte();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeInt(this.entityId);
        out.writeByte(this.entityStatus);
    }
}
