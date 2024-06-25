package org.lightsolutions.protocol.netty.codec.encrypt;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import javax.crypto.Cipher;
import java.util.List;

public class NettyLegacyEncryptionCodec extends ByteToMessageCodec<ByteBuf> {

    private final Cipher decodeCipher,encodeCipher;
    private byte[] decode = new byte[0];
    private byte[] encode = new byte[0];

    public NettyLegacyEncryptionCodec(final Cipher encode, final Cipher decode) {
        this.encodeCipher = encode;
        this.decodeCipher = decode;
    }

    /**
     * Encrypting packet
     *
     * @param channelHandlerContext context
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, ByteBuf buf2) throws Exception {
        var size = buf.readableBytes();
        var bytes = this.read(buf);
        var outputSize = encodeCipher.getOutputSize(size);

        if(this.encode.length < outputSize)
            this.encode = new byte[outputSize];

        buf2.writeBytes(this.encode, 0, encodeCipher.update(bytes, 0, size, this.encode));
    }

    /**
     * Decrypting packet
     * @param list                  Output buffer list
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> list) throws Exception {
        var size = buffer.readableBytes();
        var bytes = this.read(buffer);
        var byteBuf = ctx.alloc().heapBuffer(decodeCipher.getOutputSize(size));
        byteBuf.writerIndex(decodeCipher.update(bytes, 0, size, byteBuf.array(), byteBuf.arrayOffset()));
        list.add(byteBuf);
    }

    private byte[] read(ByteBuf buffer) {
        var i = buffer.readableBytes();
        if(decode.length < i)
            decode = new byte[i];

        buffer.readBytes(decode, 0, i);
        return decode;
    }
}
