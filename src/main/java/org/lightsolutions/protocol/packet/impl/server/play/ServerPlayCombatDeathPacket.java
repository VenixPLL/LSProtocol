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
public class ServerPlayCombatDeathPacket extends Packet {

    {
        this.setPacketId((byte) 0x3A);
    }

    private int playerId;
    private Component deathMessage;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.playerId = in.readVarInt();
        this.deathMessage = in.readComponent();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarInt(this.playerId);
        out.writeComponent(this.deathMessage);
    }
}
