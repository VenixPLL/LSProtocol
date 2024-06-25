package org.lightsolutions.protocol.data.message;

import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.lightsolutions.protocol.data.message.legacy.NBTLegacyHoverEventSerializer;

public final class ComponentSerializer {
    private static GsonComponentSerializer serializer = GsonComponentSerializer.builder()
            .legacyHoverEventSerializer(NBTLegacyHoverEventSerializer.get())
            .build();

    public static GsonComponentSerializer get() {
        return serializer;
    }

    public static void set(GsonComponentSerializer serializer) {
        ComponentSerializer.serializer = serializer;
    }

    private ComponentSerializer() {
    }
}