package org.lightsolutions.protocol.packet.impl.client.login;

import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

import java.util.Objects;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Packet.PacketInfo(connectionState = ConnectionState.LOGIN,packetDirection = PacketDirection.SERVERBOUND)
public class ClientLoginPluginResponsePacket extends Packet {

    {
        this.setPacketId((byte) 0x02);
    }

    private int messageId;
    private PacketBuffer buffer;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.messageId = in.readVarInt();
        if(in.readBoolean()) {
            try {
                this.buffer = new PacketBuffer(Unpooled.wrappedBuffer(in));
            }finally {
                in.release();
            }
        }
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarInt(this.messageId);
        if(Objects.nonNull(buffer)){
            out.writeBoolean(true);
            try {
                out.writeBytes(this.buffer);
            }finally {
                this.buffer.release();
            }
            return;
        }
        out.writeBoolean(false);
    }
}
