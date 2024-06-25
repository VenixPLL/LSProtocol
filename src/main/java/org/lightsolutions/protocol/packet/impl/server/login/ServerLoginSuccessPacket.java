package org.lightsolutions.protocol.packet.impl.server.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Packet.PacketInfo(connectionState = ConnectionState.LOGIN,packetDirection = PacketDirection.CLIENTBOUND)
public class ServerLoginSuccessPacket extends Packet {

    {
        this.setPacketId((byte) 0x02);
    }

    private UUID uuid;
    private String username;
    private Set<LoginProperty> properties;

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.uuid = in.readUuid();
        this.username = in.readStringFromBuffer(16);

        var size = in.readVarInt();
        this.properties = new HashSet<>(size,1);
        for(int i=0;i<size;i++){

            var name = in.readStringFromBuffer(Short.MAX_VALUE);
            var value = in.readStringFromBuffer(Short.MAX_VALUE);
            var signed = in.readBoolean();

            this.properties.add(new LoginProperty(name, value, signed,
                    signed ? in.readStringFromBuffer(Short.MAX_VALUE) : ""));
        }
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeUuid(this.uuid);
        out.writeString(this.username);

        out.writeVarInt(this.properties.size());
        for(var property : this.properties){
            out.writeString(property.name);
            out.writeString(property.value);
            out.writeBoolean(property.signed);
            if(property.signed) out.writeString(property.signature);
        }
    }

    record LoginProperty(String name, String value, boolean signed, String signature){}
}
