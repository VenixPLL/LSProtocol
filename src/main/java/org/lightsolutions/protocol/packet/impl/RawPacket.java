package org.lightsolutions.protocol.packet.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;
import org.lightsolutions.protocol.packet.Packet;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RawPacket extends Packet {

    /*
     * Fallback packet for packets that are not implemented.
     * Reading not implemented packets can be turned off in properties
     * (This will speed up the protocol and minimize memory usage)
     * 'read-raw' to false.
     *
     * You can also force raw packets in properties using
     * 'force-raw' to true
     * This will bypass packet registry and read all packets as RawPacket.
     */

    {
        this.setPacketId((byte) -0);
    }

    private byte[] data;
   // private PacketBuffer restBuffer;

    @Override
    public void read(PacketBuffer buffer) throws Exception {

        var buf = new byte[buffer.readableBytes()];
        buffer.readBytes(buf);
        this.data = buf;
        //this.restBuffer = new PacketBuffer(Unpooled.wrappedBuffer(buf));
    }

    @Override
    public void write(PacketBuffer buffer) throws Exception {
        try {
            buffer.writeBytes(this.data);
        }finally {
            //this.restBuffer.release();
        }
    }

    @Override
    public String toString(){
        return this.getClass().getSimpleName() + "(" + this.getPacketId() + ", Buf: " + this.data.length + ")";
    }
}
