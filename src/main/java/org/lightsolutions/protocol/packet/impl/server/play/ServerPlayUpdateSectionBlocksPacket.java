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

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Packet.PacketInfo(connectionState = ConnectionState.PLAY,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerPlayUpdateSectionBlocksPacket extends Packet{

    {
        this.setPacketId((byte) 0x47);
    }

    private Position chunkSection;
    private List<SectionBlocks> updatedBlocks;

    @Override
    public void read(PacketBuffer in) throws Exception {
        var sectionLocation = in.readLong();
        var sectionX = sectionLocation >> 42;
        var sectionY = sectionLocation << 44 >> 44;
        var sectionZ = sectionLocation << 22 >> 42;

        this.chunkSection = new Position(sectionX,sectionY,sectionZ);

        var count = in.readVarInt();
        this.updatedBlocks = new ArrayList<>(count);

        for(int i=0;i<count;i++){
            var blockData = in.readVarLong();
            var blockStateId = blockData >> 12;
            var blockLocalX = (blockData >> 8) & 0xF;
            var blockLocalY = blockData & 0xF;
            var blockLocalZ = (blockData >> 4) & 0xF;
            this.updatedBlocks.add(new SectionBlocks(blockLocalX,blockLocalY,blockLocalZ,blockStateId));
        }
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        long sectionLocation = ((long) chunkSection.posX() & 0x3FFFFL) << 42 | ((long) chunkSection.posY() & 0xFFFFFL) << 22 | ((long) chunkSection.posZ() & 0x3FFFFL);
        out.writeLong(sectionLocation);

        out.writeVarInt(updatedBlocks.size());
        for (SectionBlocks sectionBlock : updatedBlocks) {
            long blockData = (sectionBlock.blockStateId << 12) | ((long) sectionBlock.posX() & 0xF) << 8 | ((long) sectionBlock.posY() & 0xF) | ((long) sectionBlock.posZ() & 0xF) << 4;
            out.writeVarLong(blockData);
        }
    }

    public record SectionBlocks(long posX,long posY,long posZ, long blockStateId){}
}
