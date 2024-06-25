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
public class ServerPlayDisguisedChatMessagePacket extends Packet {

    {
        this.setPacketId((byte) 0x1C);
    }

    private Component message,senderName,targetName;
    private int chatType;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.message = in.readComponent();
        this.chatType = in.readVarInt();
        this.senderName = in.readComponent();
        if(in.readBoolean()) this.targetName = in.readComponent();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeComponent(this.message);
        out.writeVarInt(this.chatType);
        out.writeComponent(this.senderName);
        if(this.targetName != null){
            out.writeBoolean(true);
            out.writeComponent(this.targetName);
        }else{
            out.writeBoolean(false);
        }
    }
}
