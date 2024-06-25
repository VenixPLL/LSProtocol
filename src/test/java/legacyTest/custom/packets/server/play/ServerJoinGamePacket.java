package legacyTest.custom.packets.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Packet.PacketInfo(packetDirection = PacketDirection.CLIENTBOUND, connectionState = ConnectionState.PLAY)
public class ServerJoinGamePacket extends Packet {

    private int entityId;
    private byte gamemode;
    private byte dimension;
    private byte difficulty;
    private int maxPlayers;
    private String levelType;
    private boolean reduced_debug;

    {
        this.setPacketId((byte) 0x01);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeInt(this.entityId);
        out.writeByte(this.gamemode);//u
        out.writeByte(this.dimension);
        out.writeByte(this.difficulty);//u
        out.writeByte(this.maxPlayers);//u
        out.writeString(this.levelType);
        out.writeBoolean(this.reduced_debug);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.entityId = in.readInt();
        this.gamemode = (byte) in.readUnsignedByte();
        this.dimension = in.readByte();
        this.difficulty = (byte) in.readUnsignedByte();
        this.maxPlayers = in.readUnsignedByte();
        this.levelType = in.readStringFromBuffer(32767);
        this.reduced_debug = in.readBoolean();
    }
}
