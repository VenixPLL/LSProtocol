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
public class ServerPlayUnloadChunkPacket extends Packet {

    /*
    https://wiki.vg/Protocol#Unload_Chunk
    Note: The order is inverted, because the client reads this packet as one big-endian Long, with Z being the upper 32 bits.
    It is legal to send this packet even if the given chunk is not currently loaded.
     */

    {
        this.setPacketId((byte) 0x1F);
    }

    private int chunkX,chunkZ;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.chunkZ = in.readInt();
        this.chunkX = in.readInt();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeInt(this.chunkZ);
        out.writeInt(this.chunkX);
    }
}
