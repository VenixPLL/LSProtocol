package org.lightsolutions.protocol.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.kyori.adventure.text.Component;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Packet.PacketInfo(connectionState = ConnectionState.PLAY,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerPlayOpenScreenPacket extends Packet {

    {
        this.setPacketId((byte) 0x31);
    }

    private int windowId,windowType;
    private Component windowTitle;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.windowId = in.readVarInt();
        this.windowType = in.readVarInt();
        this.windowTitle = in.readComponent();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarInt(this.windowId);
        out.writeVarInt(this.windowType);
        out.writeComponent(this.windowTitle);
    }
}
