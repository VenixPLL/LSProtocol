package org.lightsolutions.protocol.packet.impl.server.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.kyori.adventure.text.Component;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

import java.util.UUID;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Packet.PacketInfo(connectionState = ConnectionState.CONFIG,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerConfigAddResourcePackPacket extends Packet {

    {
        this.setPacketId((byte) 0x07);
    }

    private UUID uuid;
    private String url;
    private String hash;
    private boolean forced, hasprompt;
    private Component textComponent;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.uuid = in.readUuid();
        this.url = in.readStringFromBuffer(32767);
        this.hash = in.readStringFromBuffer(40);
        this.forced = in.readBoolean();
        this.hasprompt = in.readBoolean();
        if(this.hasprompt) this.textComponent = in.readComponent();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeUuid(this.uuid);
        out.writeString(this.url);
        out.writeString(this.hash);
        out.writeBoolean(this.forced);
        out.writeBoolean(this.hasprompt);
        if(this.hasprompt) out.writeComponent(this.textComponent);
    }
}
