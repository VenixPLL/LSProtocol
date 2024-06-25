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
public class ServerPlaySetHealthPacket extends Packet {

    {
        this.setPacketId((byte) 0x5B);
    }

    private float health;
    private int food;
    private float foodSaturation;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.health = in.readFloat();
        this.food = in.readVarInt();
        this.foodSaturation = in.readFloat();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeFloat(this.health);
        out.writeVarInt(this.food);
        out.writeFloat(this.foodSaturation);
    }
}
