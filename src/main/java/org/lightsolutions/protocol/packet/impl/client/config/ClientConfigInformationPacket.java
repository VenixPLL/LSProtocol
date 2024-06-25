package org.lightsolutions.protocol.packet.impl.client.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Packet.PacketInfo(connectionState = ConnectionState.CONFIG, packetDirection = PacketDirection.SERVERBOUND)
public class ClientConfigInformationPacket extends Packet {

    {
        this.setPacketId((byte) 0x00);
    }

    private String locale;
    private byte viewDistance;
    private int chatMode;
    private boolean chatColors;
    private byte skinParts;
    private int mainHand;
    private boolean textFiltering;
    private boolean allowServerListing;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.locale = in.readStringFromBuffer(16);
        this.viewDistance = in.readByte();
        this.chatMode = in.readVarInt();
        this.chatColors = in.readBoolean();
        this.skinParts = (byte) in.readUnsignedByte();
        this.mainHand = in.readVarInt();
        this.textFiltering = in.readBoolean();
        this.allowServerListing = in.readBoolean();
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeString(this.locale);
        out.writeByte(this.viewDistance);
        out.writeVarInt(this.chatMode);
        out.writeBoolean(this.chatColors);
        out.writeByte(this.skinParts);
        out.writeVarInt(this.mainHand);
        out.writeBoolean(this.textFiltering);
        out.writeBoolean(this.allowServerListing);
    }
}
