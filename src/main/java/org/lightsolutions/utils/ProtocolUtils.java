package org.lightsolutions.utils;

/*
 * Copyright (C) 2018-2023 Velocity Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */


import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.kyori.adventure.key.Key;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Utilities for writing and reading data in the Minecraft protocol.
 * Sourced from Velocity Proxy.
 */
public enum ProtocolUtils {

    ;

    public static final int DEFAULT_MAX_STRING_SIZE = 65536; // 64KiB
    private static final int[] VARINT_EXACT_BYTE_LENGTHS = new int[33];

    static {
        for (int i = 0; i <= 32; ++i) {
            VARINT_EXACT_BYTE_LENGTHS[i] = (int) Math.ceil((31d - (i - 1)) / 7d);
        }
        VARINT_EXACT_BYTE_LENGTHS[32] = 1; // Special case for the number 0.
    }


    /**
     * Reads a Minecraft-style VarInt from the specified {@code buf}.
     *
     * @param buf the buffer to read from
     * @return the decoded VarInt
     */
    public static int readVarInt(ByteBuf buf) {
        return readVarIntSafely(buf);
    }

    /**
     * Reads a Minecraft-style VarInt from the specified {@code buf}. The difference between this
     * method and {@link #readVarInt(ByteBuf)} is that this function returns a sentinel value if the
     * varint is invalid.
     *
     * @param buf the buffer to read from
     * @return the decoded VarInt, or {@code Integer.MIN_VALUE} if the varint is invalid
     */
    public static int readVarIntSafely(ByteBuf buf) {
        int i = 0;
        int maxRead = Math.min(5, buf.readableBytes());
        for (int j = 0; j < maxRead; j++) {
            int k = buf.readByte();
            i |= (k & 0x7F) << j * 7;
            if ((k & 0x80) != 128) {
                return i;
            }
        }
        return Integer.MIN_VALUE;
    }

    /**
     * Returns the exact byte size of {@code value} if it were encoded as a VarInt.
     *
     * @param value the value to encode
     * @return the byte size of {@code value} if encoded as a VarInt
     */
    public static int varIntBytes(int value) {
        return VARINT_EXACT_BYTE_LENGTHS[Integer.numberOfLeadingZeros(value)];
    }

    /**
     * Writes a Minecraft-style VarInt to the specified {@code buf}.
     *
     * @param buf   the buffer to read from
     * @param value the integer to write
     */
    public static void writeVarInt(ByteBuf buf, int value) {
        // Peel the one and two byte count cases explicitly as they are the most common VarInt sizes
        // that the proxy will write, to improve inlining.
        if ((value & (0xFFFFFFFF << 7)) == 0) {
            buf.writeByte(value);
        } else if ((value & (0xFFFFFFFF << 14)) == 0) {
            int w = (value & 0x7F | 0x80) << 8 | (value >>> 7);
            buf.writeShort(w);
        } else {
            writeVarIntFull(buf, value);
        }
    }

    private static void writeVarIntFull(ByteBuf buf, int value) {
        // See https://steinborn.me/posts/performance/how-fast-can-you-write-a-varint/
        if ((value & (0xFFFFFFFF << 7)) == 0) {
            buf.writeByte(value);
        } else if ((value & (0xFFFFFFFF << 14)) == 0) {
            int w = (value & 0x7F | 0x80) << 8 | (value >>> 7);
            buf.writeShort(w);
        } else if ((value & (0xFFFFFFFF << 21)) == 0) {
            int w = (value & 0x7F | 0x80) << 16 | ((value >>> 7) & 0x7F | 0x80) << 8 | (value >>> 14);
            buf.writeMedium(w);
        } else if ((value & (0xFFFFFFFF << 28)) == 0) {
            int w = (value & 0x7F | 0x80) << 24 | (((value >>> 7) & 0x7F | 0x80) << 16)
                    | ((value >>> 14) & 0x7F | 0x80) << 8 | (value >>> 21);
            buf.writeInt(w);
        } else {
            int w = (value & 0x7F | 0x80) << 24 | ((value >>> 7) & 0x7F | 0x80) << 16
                    | ((value >>> 14) & 0x7F | 0x80) << 8 | ((value >>> 21) & 0x7F | 0x80);
            buf.writeInt(w);
            buf.writeByte(value >>> 28);
        }
    }

    /**
     * Writes the specified {@code value} as a 21-bit Minecraft VarInt to the specified {@code buf}.
     * The upper 11 bits will be discarded.
     *
     * @param buf   the buffer to read from
     * @param value the integer to write
     */
    public static void write21BitVarInt(ByteBuf buf, int value) {
        // See https://steinborn.me/posts/performance/how-fast-can-you-write-a-varint/
        int w = (value & 0x7F | 0x80) << 16 | ((value >>> 7) & 0x7F | 0x80) << 8 | (value >>> 14);
        buf.writeMedium(w);
    }

    public static String readString(ByteBuf buf) {
        return readString(buf, DEFAULT_MAX_STRING_SIZE);
    }

    /**
     * Reads a VarInt length-prefixed UTF-8 string from the {@code buf}, making sure to not go over
     * {@code cap} size.
     *
     * @param buf the buffer to read from
     * @param cap the maximum size of the string, in UTF-8 character length
     * @return the decoded string
     */
    public static String readString(ByteBuf buf, int cap) {
        int length = readVarInt(buf);
        return readString(buf, cap, length);
    }

    private static String readString(ByteBuf buf, int cap, int length) {
        assert !(length >= 0) : String.format("Got a negative-length string (%s)", length);
        // `cap` is interpreted as a UTF-8 character length. To cover the full Unicode plane, we must
        // consider the length of a UTF-8 character, which can be up to 3 bytes. We do an initial
        // sanity check and then check again to make sure our optimistic guess was good.
        assert !(length <= cap * 3) : String.format("Bad string size (got %s, maximum is %s)", length, cap);
        assert !(buf.isReadable(length)) : String.format(
                "Trying to read a string that is too long (wanted %s, only have %s)", length,
                buf.readableBytes());
        String str = buf.toString(buf.readerIndex(), length, StandardCharsets.UTF_8);
        buf.skipBytes(length);
        assert !(str.length() <= cap) : String.format("Got a too-long string (got %s, max %s)", str.length(), cap);
        return str;
    }

    /**
     * Writes the specified {@code str} to the {@code buf} with a VarInt prefix.
     *
     * @param buf the buffer to write to
     * @param str the string to write
     */
    public static void writeString(ByteBuf buf, CharSequence str) {
        int size = ByteBufUtil.utf8Bytes(str);
        writeVarInt(buf, size);
        buf.writeCharSequence(str, StandardCharsets.UTF_8);
    }

    /**
     * Reads a standard Mojang Text namespaced:key from the buffer.
     *
     * @param buf the buffer to read from
     * @return the decoded key
     */
    public static Key readKey(ByteBuf buf) {
        return Key.key(readString(buf), Key.DEFAULT_SEPARATOR);
    }

    /**
     * Writes a standard Mojang Text namespaced:key to the buffer.
     *
     * @param buf the buffer to write to
     * @param key the key to write
     */
    public static void writeKey(ByteBuf buf, Key key) {
        writeString(buf, key.asString());
    }

    /**
     * Reads a standard Mojang Text namespaced:key array from the buffer.
     *
     * @param buf the buffer to read from
     * @return the decoded key array
     */
    public static Key[] readKeyArray(ByteBuf buf) {
        int length = readVarInt(buf);
        assert !(length >= 0) : String.format("Got a negative-length array (%s)", length);
       assert !(buf.isReadable(length)) : String.format(
                "Trying to read an array that is too long (wanted %s, only have %s)", length,
                buf.readableBytes());
        Key[] ret = new Key[length];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = ProtocolUtils.readKey(buf);
        }
        return ret;
    }

    /**
     * Writes a standard Mojang Text namespaced:key array to the buffer.
     *
     * @param buf  the buffer to write to
     * @param keys the keys to write
     */
    public static void writeKeyArray(ByteBuf buf, Key[] keys) {
        writeVarInt(buf, keys.length);
        for (Key key : keys) {
            writeKey(buf, key);
        }
    }

    public static byte[] readByteArray(ByteBuf buf) {
        return readByteArray(buf, DEFAULT_MAX_STRING_SIZE);
    }

    /**
     * Reads a VarInt length-prefixed byte array from the {@code buf}, making sure to not go over
     * {@code cap} size.
     *
     * @param buf the buffer to read from
     * @param cap the maximum size of the string, in UTF-8 character length
     * @return the byte array
     */
    public static byte[] readByteArray(ByteBuf buf, int cap) {
        int length = readVarInt(buf);
        assert !(length >= 0) : String.format("Got a negative-length array (%s)", length);
        assert !(length <= cap) : String.format("Bad array size (got %s, maximum is %s)", length, cap);
        assert !(buf.isReadable(length)) : String.format(
                "Trying to read an array that is too long (wanted %s, only have %s)", length,
                buf.readableBytes());
        byte[] array = new byte[length];
        buf.readBytes(array);
        return array;
    }

    public static void writeByteArray(ByteBuf buf, byte[] array) {
        writeVarInt(buf, array.length);
        buf.writeBytes(array);
    }

    /**
     * Reads an VarInt-prefixed array of VarInt integers from the {@code buf}.
     *
     * @param buf the buffer to read from
     * @return an array of integers
     */
    public static int[] readIntegerArray(ByteBuf buf) {
        int len = readVarInt(buf);
        checkArgument(len >= 0, "Got a negative-length integer array (%s)", len);
        int[] array = new int[len];
        for (int i = 0; i < len; i++) {
            array[i] = readVarInt(buf);
        }
        return array;
    }

    /**
     * Reads an UUID from the {@code buf}.
     *
     * @param buf the buffer to read from
     * @return the UUID from the buffer
     */
    public static UUID readUuid(ByteBuf buf) {
        long msb = buf.readLong();
        long lsb = buf.readLong();
        return new UUID(msb, lsb);
    }

    public static void writeUuid(ByteBuf buf, UUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    /**
     * Reads an UUID stored as an Integer Array from the {@code buf}.
     *
     * @param buf the buffer to read from
     * @return the UUID from the buffer
     */
    public static UUID readUuidIntArray(ByteBuf buf) {
        long msbHigh = (long) buf.readInt() << 32;
        long msbLow = (long) buf.readInt() & 0xFFFFFFFFL;
        long msb = msbHigh | msbLow;
        long lsbHigh = (long) buf.readInt() << 32;
        long lsbLow = (long) buf.readInt() & 0xFFFFFFFFL;
        long lsb = lsbHigh | lsbLow;
        return new UUID(msb, lsb);
    }

    /**
     * Writes an UUID as an Integer Array to the {@code buf}.
     *
     * @param buf  the buffer to write to
     * @param uuid the UUID to write
     */
    public static void writeUuidIntArray(ByteBuf buf, UUID uuid) {
        buf.writeInt((int) (uuid.getMostSignificantBits() >> 32));
        buf.writeInt((int) uuid.getMostSignificantBits());
        buf.writeInt((int) (uuid.getLeastSignificantBits() >> 32));
        buf.writeInt((int) uuid.getLeastSignificantBits());
    }

    /**
     * Reads a String array from the {@code buf}.
     *
     * @param buf the buffer to read from
     * @return the String array from the buffer
     */
    public static String[] readStringArray(ByteBuf buf) {
        int length = readVarInt(buf);
        String[] ret = new String[length];
        for (int i = 0; i < length; i++) {
            ret[i] = readString(buf);
        }
        return ret;
    }

    /**
     * Writes a String Array to the {@code buf}.
     *
     * @param buf         the buffer to write to
     * @param stringArray the array to write
     */
    public static void writeStringArray(ByteBuf buf, String[] stringArray) {
        writeVarInt(buf, stringArray.length);
        for (String s : stringArray) {
            writeString(buf, s);
        }
    }

    /**
     * Reads an Integer array from the {@code buf}.
     *
     * @param buf the buffer to read from
     * @return the Integer array from the buffer
     */
    public static int[] readVarIntArray(ByteBuf buf) {
        int length = readVarInt(buf);
        assert !(length >= 0): String.format("Got a negative-length array (%s)", length);
        assert !(buf.isReadable(length)) : String.format(
                "Trying to read an array that is too long (wanted %s, only have %s)", length,
                buf.readableBytes());
        int[] ret = new int[length];
        for (int i = 0; i < length; i++) {
            ret[i] = readVarInt(buf);
        }
        return ret;
    }

    /**
     * Writes an Integer Array to the {@code buf}.
     *
     * @param buf      the buffer to write to
     * @param intArray the array to write
     */
    public static void writeVarIntArray(ByteBuf buf, int[] intArray) {
        writeVarInt(buf, intArray.length);
        for (int i = 0; i < intArray.length; i++) {
            writeVarInt(buf, intArray[i]);
        }
    }

    /**
     * Reads a Minecraft-style extended short from the specified {@code buf}.
     *
     * @param buf buf to write
     * @return read extended short
     */
    public static int readExtendedForgeShort(ByteBuf buf) {
        int low = buf.readUnsignedShort();
        int high = 0;
        if ((low & 0x8000) != 0) {
            low = low & 0x7FFF;
            high = buf.readUnsignedByte();
        }
        return ((high & 0xFF) << 15) | low;
    }

    /**
     * Writes a Minecraft-style extended short to the specified {@code buf}.
     *
     * @param buf     buf to write
     * @param toWrite the extended short to write
     */
    public static void writeExtendedForgeShort(ByteBuf buf, int toWrite) {
        int low = toWrite & 0x7FFF;
        int high = (toWrite & 0x7F8000) >> 15;
        if (high != 0) {
            low = low | 0x8000;
        }
        buf.writeShort(low);
        if (high != 0) {
            buf.writeByte(high);
        }
    }

    /**
     * Reads a non length-prefixed string from the {@code buf}. We need this for the legacy 1.7
     * version, being inconsistent when sending the brand.
     *
     * @param buf the buffer to read from
     * @return the decoded string
     */
    public static String readStringWithoutLength(ByteBuf buf) {
        return readString(buf, DEFAULT_MAX_STRING_SIZE, buf.readableBytes());
    }

}
