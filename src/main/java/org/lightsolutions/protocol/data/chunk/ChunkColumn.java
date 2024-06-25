package org.lightsolutions.protocol.data.chunk;

import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;

import java.io.IOException;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class ChunkColumn {

    private final int x,z;
    private final ChunkSection[] chunkSections = new ChunkSection[24]; //TODO Assume vanilla height

    public ChunkColumn(int x,int z,byte[] data) throws IOException {
        this.x = x;
        this.z = z;
        this.readFromData(data);
    }
    public byte[] writeToData(){
        var buffer = new PacketBuffer(Unpooled.buffer());
        for (ChunkSection chunkSection : chunkSections) {
            buffer.writeChunkSection(chunkSection);
        }

        var bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(buffer);
        return bytes;
    }


    public void readFromData(byte[] data) throws IOException {
        this.readFromBuffer(new PacketBuffer(Unpooled.wrappedBuffer(data)));
    }

    public void readFromBuffer(PacketBuffer buffer) throws IOException {
        var y = 0;
        while(buffer.readableBytes() != 0) {
            chunkSections[y] = buffer.readChunkSection();
            y++;
        }
    }

    public void setBlockOnSection(int cy,int bx,int by,int bz, int blockState){
        var chunk = chunkSections[cy];
        if(Objects.nonNull(chunk)) chunk.setBlock(bx,by,bz,blockState);
    }

    public void setBlock(int x, int y, int z, int blockState) {
        var cy = (y + 64) >> 4;
        var chunk = chunkSections[cy];
        if (Objects.nonNull(chunk)) {
            var bx = x & 15;
            var by = y & 15;
            var bz = z & 15;
            chunk.setBlock(bx, by, bz, blockState);
        }
    }

    public int getBlock(int x, int y, int z) {
        int cy = (y + 64) >> 4;

        var bx = x & 15;
        var by = y & 15;
        var bz = z & 15;

        var chunk = chunkSections[cy];
        if (Objects.nonNull(chunk))
            return chunk.getBlock(bx, by, bz);


        return -1;
    }



}
