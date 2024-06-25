package com.github.steveice10.opennbt.tag;

import com.github.steveice10.opennbt.tag.builtin.*;
import com.github.steveice10.opennbt.tag.builtin.custom.DoubleArrayTag;
import com.github.steveice10.opennbt.tag.builtin.custom.FloatArrayTag;
import com.github.steveice10.opennbt.tag.builtin.custom.ShortArrayTag;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * A registry containing different tag classes.
 */
public class TagRegistry {
    private static final Map<Integer, Class<? extends Tag>> idToTag = new HashMap<Integer, Class<? extends Tag>>();
    private static final Map<Class<? extends Tag>, Integer> tagToId = new HashMap<Class<? extends Tag>, Integer>();

    static {
        register(1, ByteTag.class);
        register(2, ShortTag.class);
        register(3, IntTag.class);
        register(4, LongTag.class);
        register(5, FloatTag.class);
        register(6, DoubleTag.class);
        register(7, ByteArrayTag.class);
        register(8, StringTag.class);
        register(9, ListTag.class);
        register(10, CompoundTag.class);
        register(11, IntArrayTag.class);
        register(12, LongArrayTag.class);

        register(60, DoubleArrayTag.class);
        register(61, FloatArrayTag.class);
        register(65, ShortArrayTag.class);
    }

    /**
     * Registers a tag class.
     *
     * @param id  ID of the tag.
     * @param tag Tag class to register.
     * @throws TagRegisterException If an error occurs while registering the tag.
     */
    public static void register(int id, Class<? extends Tag> tag) throws TagRegisterException {
        if(idToTag.containsKey(id)) {
            throw new TagRegisterException("Tag ID \"" + id + "\" is already in use.");
        }

        if(tagToId.containsKey(tag)) {
            throw new TagRegisterException("Tag \"" + tag.getSimpleName() + "\" is already registered.");
        }

        idToTag.put(id, tag);
        tagToId.put(tag, id);
    }

    /**
     * Unregisters a tag class.
     *
     * @param id  ID of the tag to unregister.
     */
    public static void unregister(int id) {
        tagToId.remove(getClassFor(id));
        idToTag.remove(id);
    }

    /**
     * Gets the tag class with the given id.
     *
     * @param id Id of the tag.
     * @return The tag class with the given id, or null if it cannot be found.
     */
    public static Class<? extends Tag> getClassFor(int id) {
        if(!idToTag.containsKey(id)) {
            return null;
        }

        return idToTag.get(id);
    }

    /**
     * Gets the id of the given tag class.
     *
     * @param clazz The tag class to get the id of.
     * @return The id of the given tag class, or -1 if it cannot be found.
     */
    public static int getIdFor(Class<? extends Tag> clazz) {
        if(!tagToId.containsKey(clazz)) {
            return -1;
        }

        return tagToId.get(clazz);
    }

    /**
     * Creates an instance of the tag with the given id, using the String constructor.
     *
     * @param id      Id of the tag.
     * @param tagName Name to give the tag.
     * @return The created tag.
     * @throws TagCreateException If an error occurs while creating the tag.
     */
    public static Tag createInstance(int id, String tagName) throws TagCreateException {
        Class<? extends Tag> clazz = idToTag.get(id);
        if(clazz == null) {
            throw new TagCreateException("Could not find tag with ID \"" + id + "\".");
        }

        try {
            Constructor<? extends Tag> constructor = clazz.getDeclaredConstructor(String.class);
            constructor.setAccessible(true);
            return constructor.newInstance(tagName);
        } catch(Exception e) {
            throw new TagCreateException("Failed to create instance of tag \"" + clazz.getSimpleName() + "\".", e);
        }
    }
}
