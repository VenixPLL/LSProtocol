package org.lightsolutions.protocol.packet;

import lombok.Getter;
import lombok.Setter;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Setter
@Getter
public abstract class Packet {

    private byte packetId = -0x01;

    public abstract void read(PacketBuffer in) throws Exception;

    public abstract void write(PacketBuffer out) throws Exception;


    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface PacketInfo {
        boolean universal = false;
        ConnectionState connectionState();

        PacketDirection packetDirection();

    }
}
