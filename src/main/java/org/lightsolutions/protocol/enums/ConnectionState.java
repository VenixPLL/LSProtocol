package org.lightsolutions.protocol.enums;

import lombok.Getter;

@Getter
public enum ConnectionState {

    HANDSHAKE((byte) 0),
    STATUS((byte) 1),
    LOGIN((byte) 2),
    CONFIG((byte)3),
    PLAY((byte) 4);

    final byte id;

    ConnectionState(byte id){
        this.id = id;
    }
}
