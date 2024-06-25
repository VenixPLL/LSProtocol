package org.lightsolutions.protocol.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.kyori.adventure.text.Component;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
//@Packet.PacketInfo(connectionState = ConnectionState.PLAY,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerPlaySystemChatMessagePacket extends Packet { //TODO Fix this?

    {
        this.setPacketId((byte) 0x69);
    }

    private Component content;
    private boolean overlay;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.content = in.readComponent();
        this.overlay = in.readBoolean();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeComponent(this.content);
        out.writeBoolean(this.overlay);
    }
}
