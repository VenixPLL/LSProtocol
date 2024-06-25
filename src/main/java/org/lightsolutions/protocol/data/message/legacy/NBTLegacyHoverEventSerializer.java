package org.lightsolutions.protocol.data.message.legacy;

import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.json.LegacyHoverEventSerializer;
import org.jetbrains.annotations.NotNull;

/**
 * A legacy {@link HoverEvent} serializer.
 *
 * @since 4.14.0
 */
public interface NBTLegacyHoverEventSerializer extends LegacyHoverEventSerializer {
    /**
     * Gets the legacy {@link HoverEvent} serializer.
     *
     * @return a legacy {@link HoverEvent} serializer
     * @since 4.14.0
     */
    static @NotNull LegacyHoverEventSerializer get() {
        return NBTLegacyHoverEventSerializerImpl.INSTANCE;
    }
}
