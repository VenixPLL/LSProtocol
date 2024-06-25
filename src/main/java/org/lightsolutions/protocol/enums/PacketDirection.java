package org.lightsolutions.protocol.enums;

import lombok.Getter;

@Getter
public enum PacketDirection {

    CLIENTBOUND((byte) 0),
    SERVERBOUND((byte) 1);

    final byte id;
    PacketDirection(byte id){
        this.id = id;
    }

}
