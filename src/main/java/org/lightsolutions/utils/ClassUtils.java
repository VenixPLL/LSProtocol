package org.lightsolutions.utils;

import org.lightsolutions.LightProtocol;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassUtils {

    public static Set<Class<?>> getClasses(String packageName){
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        if(Objects.isNull(stream)) {
            LightProtocol.getLogger().warning("No classes present in " + packageName);
            return Collections.emptySet();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> {
                    try {
                        return getClass(line, packageName);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());
    }

    public static Class<?> getClass(String className, String packageName) throws ClassNotFoundException{
        return Class.forName(packageName + "."
                + className.substring(0, className.lastIndexOf('.')));
    }

}
