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

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Packet.PacketInfo(connectionState = ConnectionState.PLAY,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerPlayCommandSuggestionsResponsePacket extends Packet {

    {
        this.setPacketId((byte) 0x10);
    }

    private int transactionId;
    private int textStart;
    private int textLength;
    private List<CommandMatches> matches;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.transactionId = in.readVarInt();
        this.textStart = in.readVarInt();
        this.textLength = in.readVarInt();

        var count = in.readVarInt();
        this.matches = new ArrayList<>(count);
        for(int i=0;i<count;i++){
            var match = in.readStringFromBuffer(32767);
            Component tooltip = null;
            if(in.readBoolean()) tooltip = in.readComponent();

            this.matches.add(new CommandMatches(match,tooltip));
        }
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarInt(this.transactionId);
        out.writeVarInt(this.textStart);
        out.writeVarInt(this.textLength);

        out.writeVarInt(this.matches.size());
        for(CommandMatches match : this.matches){
            out.writeString(match.match);
            if(match.tooltip != null){
                out.writeBoolean(true);
                out.writeComponent(match.tooltip);
            }else{
                out.writeBoolean(false);
            }
        }
    }

    public record CommandMatches(String match, Component tooltip) {}
}
