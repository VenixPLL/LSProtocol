package org.lightsolutions.protocol.netty.codec.compress;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.DecoderException;
import org.lightsolutions.protocol.netty.ProtocolAttributes;
import org.lightsolutions.protocol.netty.buffer.PacketBuffer;

import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Legacy GZIP Java Inflater/Deflater compression codec
 * Slower but works!
 */
public class NettyLegacyCompressionCodec extends ByteToMessageCodec<ByteBuf> {

    private final byte[] buffer = new byte[8192];
    private final Deflater deflater;
    private final Inflater inflater;

    public NettyLegacyCompressionCodec() {
        this.deflater = new Deflater();
        this.inflater = new Inflater();
    }

    /**
     * Compressing packet with desired threshold
     *
     * @param channelHandlerContext
     * @param in                    Input uncompressed buffer
     * @param out                   Output compressed buffer
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf in, ByteBuf out) throws Exception {
        var threshold = channelHandlerContext.channel().attr(ProtocolAttributes.compressionThresholdKey).get();
        var readable = in.readableBytes();
        var output = new PacketBuffer(out);
        if(readable < threshold) {
            output.writeVarInt(0);
            out.writeBytes(in);
        } else {
            var bytes = new byte[readable];
            in.readBytes(bytes);
            output.writeVarInt(bytes.length);
            this.deflater.setInput(bytes, 0, readable);
            this.deflater.finish();
            while(!this.deflater.finished()) {
                var length = this.deflater.deflate(this.buffer);
                output.writeBytes(buffer, length);
            }

            this.deflater.reset();
        }
    }

    /**
     * Decompressing packet with desired threshold
     *
     * @param channelHandlerContext
     * @param buf                   Input compressed Buffer
     * @param out                   Output decompressed Buffer list
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, List<Object> out) throws Exception {
        if(buf.readableBytes() != 0) {
            var in = new PacketBuffer(buf);
            var size = in.readVarInt();
            if(size == 0) {
                out.add(buf.readBytes(buf.readableBytes()));
            } else {
                final var threshold = channelHandlerContext.channel().attr(ProtocolAttributes.compressionThresholdKey).get();
                if(size < threshold) {
                    throw new DecoderException("Badly compressed packet: size of " + size + " is below threshold of " + threshold + ".");
                }

                if(size > 2097152) {
                    throw new DecoderException("Badly compressed packet: size of " + size + " is larger than protocol maximum of " + 2097152 + ".");
                }

                var bytes = new byte[buf.readableBytes()];
                in.readBytes(bytes);
                this.inflater.setInput(bytes);
                var inflated = new byte[size];
                this.inflater.inflate(inflated);
                out.add(Unpooled.wrappedBuffer(inflated));
                this.inflater.reset();
            }
        }
    }
}
