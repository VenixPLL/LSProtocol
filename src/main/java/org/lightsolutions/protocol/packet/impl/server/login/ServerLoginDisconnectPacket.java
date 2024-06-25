package org.lightsolutions.protocol.packet.impl.server.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.kyori.adventure.text.Component;
import org.lightsolutions.protocol.data.message.ComponentSerializer;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Packet.PacketInfo(connectionState = ConnectionState.LOGIN,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerLoginDisconnectPacket extends Packet {

    {
        this.setPacketId((byte) 0x00);
    }

    private Component reason;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.reason = ComponentSerializer.get().deserialize(in.readStringFromBuffer(262144));
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeString(ComponentSerializer.get().serialize(this.reason));
    }
}
