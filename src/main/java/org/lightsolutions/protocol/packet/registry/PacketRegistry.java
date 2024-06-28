package org.lightsolutions.protocol.packet.registry;

import org.lightsolutions.LightProtocol;
import org.lightsolutions.protocol.enums.ConnectionState;
import org.lightsolutions.protocol.enums.PacketDirection;
import org.lightsolutions.protocol.packet.Packet;
import org.lightsolutions.protocol.packet.registry.element.PacketRegistryElement;
import org.lightsolutions.utils.ClassUtils;

import java.lang.reflect.Constructor;
import java.util.EnumMap;

public class PacketRegistry {

    private final String[] PATH_EXTENSIONS = new String[]{
            "client.handshake","client.login","client.config","client.status","client.play",
            "server.login","server.config","server.status","server.play"
    };
    private static final EnumMap<ConnectionState, PacketRegistryElement> packetElements = new EnumMap<>(ConnectionState.class);

    public void registerPacket(ConnectionState connectionState, PacketDirection packetDirection,Packet packet){
        packetElements.merge(connectionState,new PacketRegistryElement(), (prev, current) -> prev)
                .subRegister(packetDirection,packet);
    }

    public void unregisterPacket(ConnectionState connectionState, PacketDirection packetDirection, byte packetId){
        var element = packetElements.get(connectionState);
        assert element != null : "Connection state not found in registry";
        element.subUnregister(packetDirection,packetId);
    }

    public Packet getPacket(ConnectionState connectionState, PacketDirection packetDirection, byte id){
        //Int -> byte conversion to save memory ¯\_(ツ)_/¯
        assert id < Byte.MAX_VALUE : "Packet id out of bounds!";
        if(!packetElements.containsKey(connectionState)) return null;
        return this.getNewInstance(packetElements.get(connectionState).getPacket(packetDirection,id));
    }

    /**
     * Creating new packet instance
     * @param packetIn Packet to instantiate
     * @return New packet instance
     */
    private Packet getNewInstance(final Packet packetIn) {
        if (packetIn == null) return null;
        Class<? extends Packet> packet = packetIn.getClass();
        try {
            Constructor<? extends Packet> constructor = packet.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to instantiate packet \"" + packetIn.getPacketId() + ", " + packet.getName() + "\".", e);
        }
    }

    public void initializeRegistry(){

        String defaultPath = "org.lightsolutions.protocol.packet.impl";
        if(LightProtocol.getInstance().getProtocolProperty().has("custom-packets-path")){
            defaultPath = (String) LightProtocol.getInstance().getProtocolProperty().getProperty("custom-packets-path");
            LightProtocol.getLogger().info("Using custom packets path: " + defaultPath);
        }

        //CLIENT
        final var sT = System.currentTimeMillis();
        for(var extension : this.PATH_EXTENSIONS){
            this.loadPacketsFromPackage(defaultPath + "." + extension);
        }

        final var eT = System.currentTimeMillis() - sT;
        LightProtocol.getLogger().info("PacketRegistry took " + eT + "ms to load packets!");
    }

    public void loadPacketsFromPackage(String packageName){
        try {
            ClassUtils.getClasses(packageName).forEach(aClass -> {
                if (aClass.isAnnotationPresent(Packet.PacketInfo.class)) {
                    try {
                        Constructor<?> constructor = aClass.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        this.registerPacketObject((Packet) constructor.newInstance());
                    } catch (Exception e) {
                        LightProtocol.getLogger().severe("Failed to register packet " + aClass.getSimpleName());
                    }
                }
            });
        }catch(Exception e){
            LightProtocol.getLogger().warning("Package name " + packageName + " not found when registering packets!");
        }
    }

    private void registerPacketObject(Packet packet){
        final var info = packet.getClass().getAnnotation(Packet.PacketInfo.class);
        if(info.universal) {
            this.registerPacket(info.connectionState(), PacketDirection.CLIENTBOUND, packet);
            this.registerPacket(info.connectionState(), PacketDirection.SERVERBOUND, packet);
        }else {
            this.registerPacket(info.connectionState(), info.packetDirection(), packet);
        }
    }

    public void invalidate(){
        packetElements.clear();
    }
}
