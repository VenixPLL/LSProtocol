package org.lightsolutions.protocol.packet.registry.element;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.packet.Packet;

import java.util.HashMap;

@Getter
@RequiredArgsConstructor
public class PacketRegistryElement {

    /**
     * You might ask why im using byte instead of int in packetId,
     * id is represented as VarInt -> <a href="https://wiki.vg/Protocol#Packet_format">...</a>
     * But max packet id as of 1.20.4 is 116
     * So we can use byte instead of allocating all 32 bits for an integer
     * Thus saving memory on every one of them (yeah drastic measures)
     */

    private final HashMap<Byte, Packet> CLIENT_BOUND = new HashMap<>(6,0.9f);
    private final HashMap<Byte, Packet> SERVER_BOUND = new HashMap<>(6,0.9f);

    public void subRegister(PacketDirection direction, Packet packet){
        final byte packetId = (byte) packet.getPacketId();
        switch(direction){
            case SERVERBOUND -> SERVER_BOUND.put(packetId,packet);
            case CLIENTBOUND -> CLIENT_BOUND.put(packetId,packet);
        }
    }

    public void subUnregister(PacketDirection direction, byte packetId) {
        switch(direction){
            case SERVERBOUND -> SERVER_BOUND.remove(packetId);
            case CLIENTBOUND -> CLIENT_BOUND.remove(packetId);
        };
    }

    public Packet getPacket(PacketDirection direction, byte packetId){
        return switch(direction){
            case SERVERBOUND -> SERVER_BOUND.get(packetId);
            case CLIENTBOUND -> CLIENT_BOUND.get(packetId);
        };
    }

}
