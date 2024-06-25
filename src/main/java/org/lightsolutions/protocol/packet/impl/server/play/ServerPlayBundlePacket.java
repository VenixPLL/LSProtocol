package org.lightsolutions.protocol.packet.impl.server.play;

import lombok.Getter;
import lombok.ToString;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@Packet.PacketInfo(connectionState = ConnectionState.PLAY,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerPlayBundlePacket extends Packet {

    {
        this.setPacketId((byte) 0x00);
    }

    private List<Packet> packets = new ArrayList<>();

    @Override
    public void read(PacketBuffer in) throws Exception {
    }

    @Override
    public void write(PacketBuffer out) throws Exception {

    }



}
