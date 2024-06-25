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
public class ServerPlayAwardStatisticsPacket extends Packet {

    {
        this.setPacketId((byte) 0x04);
    }

    private List<Statistics> statistics;

    @Override
    public void read(PacketBuffer in) throws Exception {
        var count = in.readVarInt();
        this.statistics = new ArrayList<>(count);
        for(int i=0;i<count;i++){
            this.statistics.add(new Statistics(in.readVarInt(),in.readVarInt(),in.readVarInt()));
        }
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarInt(this.statistics.size());
        for(Statistics stat : this.statistics){
            out.writeVarInt(stat.categoryId);
            out.writeVarInt(stat.statisticId);
            out.writeVarInt(stat.value);
        }
    }

    private record Statistics(int categoryId, int statisticId, int value){}
}
