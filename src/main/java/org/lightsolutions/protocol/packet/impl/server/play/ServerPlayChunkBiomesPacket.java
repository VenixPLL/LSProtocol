package org.lightsolutions.protocol.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Packet.PacketInfo(connectionState = ConnectionState.PLAY,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerPlayChunkBiomesPacket extends Packet {

    {
        this.setPacketId((byte) 0x0E);
    }

    private List<ChunkBiome> biomes;

    @Override
    public void read(PacketBuffer in) throws Exception {
        var size = in.readVarInt();
        this.biomes = new ArrayList<>(size);

        for(int i=0;i<size;i++)
            this.biomes.add(new ChunkBiome(in.readInt(),in.readInt(),in.readByteArray()));

    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarInt(this.biomes.size());
        for(ChunkBiome biome : this.biomes){
            out.writeInt(biome.chunkX);
            out.writeInt(biome.chunkZ);
            out.writeByteArray(biome.data);
        }
    }

    public record ChunkBiome(int chunkX, int chunkZ, byte[] data) {}
}
