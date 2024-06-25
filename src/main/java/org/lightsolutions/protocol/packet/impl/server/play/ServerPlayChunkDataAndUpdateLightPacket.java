package org.lightsolutions.protocol.packet.impl.server.play;

import com.github.steveice10.opennbt.tag.builtin.Tag;
import lombok.*;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

@Getter
@ToString
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@Packet.PacketInfo(connectionState = ConnectionState.PLAY,packetDirection = PacketDirection.CLIENTBOUND) //TODO Broken somehow?
public class ServerPlayChunkDataAndUpdateLightPacket extends Packet {

    {
        this.setPacketId((byte) 0x25);
    }

    private int chunkX,chunkZ;
    private Tag heightmaps;
    private byte[] chunkData;

    private List<BlockEntity> blockEntities;

    private BitSet skyLightMask,blockLightMask,emptySkyLightMask,emptyBlockLightMask;

    private List<byte[]> skyLights;
    private List<byte[]> blockLights;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.chunkX = in.readInt();
        this.chunkZ = in.readInt();
        this.heightmaps = in.readAnyTag();

        this.chunkData = new byte[in.readVarInt()];
        in.readBytes(this.chunkData);

        var length = in.readVarInt();
        this.blockEntities = new ArrayList<>(length);
        for(int i=0;i<length;i++){
            var packedXZ = in.readUnsignedByte();
            var x = packedXZ >> 4;
            var y = in.readShort();
            var z = packedXZ & 15;
            var type = in.readVarInt();
            var data = in.readAnyTag();

            this.blockEntities.add(new BlockEntity(x,y,z,type,data));
        }

        this.skyLightMask = BitSet.valueOf(in.readLongArray(null));
        this.blockLightMask = BitSet.valueOf(in.readLongArray(null));
        this.emptySkyLightMask = BitSet.valueOf(in.readLongArray(null));
        this.emptyBlockLightMask = BitSet.valueOf(in.readLongArray(null));

        var skyLightArraySize = in.readVarInt();
        this.skyLights = new ArrayList<>(skyLightArraySize);
        for(int i=0;i<skyLightArraySize;i++){
            var skylight = new byte[in.readVarInt()];
            in.readBytes(skylight);
            this.skyLights.add(skylight);
        }

        var blockLightArraySize = in.readVarInt();
        this.blockLights = new ArrayList<>(blockLightArraySize);
        for(int i=0;i<blockLightArraySize;i++){
            var block_light = new byte[in.readVarInt()];
            in.readBytes(block_light);
            this.blockLights.add(block_light);
        }

    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeInt(chunkX);
        out.writeInt(chunkZ);
        out.writeAnyTag(heightmaps);

        out.writeVarInt(chunkData.length);
        out.writeBytes(chunkData);

        out.writeVarInt(blockEntities.size());
        for (BlockEntity blockEntity : blockEntities) {
            int packedXZ = (blockEntity.x() << 4) | (blockEntity.z() & 0xF);
            out.writeByte(packedXZ);
            out.writeShort(blockEntity.y());
            out.writeVarInt(blockEntity.type());
            out.writeAnyTag(blockEntity.data());
        }

        out.writeLongArray(skyLightMask.toLongArray());
        out.writeLongArray(blockLightMask.toLongArray());
        out.writeLongArray(emptySkyLightMask.toLongArray());
        out.writeLongArray(emptyBlockLightMask.toLongArray());

        out.writeVarInt(skyLights.size());
        for (byte[] skyLight : skyLights) {
            out.writeVarInt(skyLight.length);
            out.writeBytes(skyLight);
        }

        out.writeVarInt(blockLights.size());
        for (byte[] blockLight : blockLights) {
            out.writeVarInt(blockLight.length);
            out.writeBytes(blockLight);
        }
    }

    record BlockEntity(int x,int y,int z,int type, Tag data) {}
}
