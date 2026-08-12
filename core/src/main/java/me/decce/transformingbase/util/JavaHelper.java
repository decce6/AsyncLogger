package me.decce.transformingbase.util;

import java.lang.reflect.InvocationTargetException;

public class JavaHelper {
    public static int major() {
        try {
            var versionMethod = Runtime.class.getMethod("version"); // since Java 9
            var version = versionMethod.invoke(null);
            return (int) version.getClass().getMethod("major").invoke(version); // since Java 9 (deprecated in Java 10 - maybe change to use feature() instead?)
        } catch (NoSuchMethodException e) {
            return 8;
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
