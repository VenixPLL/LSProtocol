package org.lightsolutions.protocol.property;

import java.util.HashMap;


public final class ProtocolProperty {

    private final HashMap<String,Object> propertyMap = new HashMap<>();

    public void setProperty(String key, Object value){
        this.propertyMap.put(key,value);
    }

    public Object getProperty(String key){
        return this.propertyMap.get(key);
    }

    public boolean has(String key){
        return this.propertyMap.containsKey(key);
    }
}
