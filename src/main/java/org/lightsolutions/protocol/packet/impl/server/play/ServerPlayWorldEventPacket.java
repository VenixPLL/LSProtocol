package org.lightsolutions.protocol.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.lightsolutions.protocol.data.Position;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Packet.PacketInfo(connectionState = ConnectionState.PLAY,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerPlayWorldEventPacket extends Packet {

    {
        this.setPacketId((byte) 0x26);
    }

    private int eventId;
    private Position eventPos;
    private int eventData;
    private boolean disableRelativeVolume;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.eventData = in.readInt();
        this.eventPos = in.readPosition();
        this.eventData = in.readInt();
        this.disableRelativeVolume = in.readBoolean();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeInt(this.eventId);
        out.writePosition(this.eventPos);
        out.writeInt(this.eventData);
        out.writeBoolean(this.disableRelativeVolume);
    }
}
