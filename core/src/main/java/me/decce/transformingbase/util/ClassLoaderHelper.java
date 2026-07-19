package me.decce.transformingbase.util;

import java.lang.invoke.MethodHandle;

import static me.decce.transformingbase.util.ReflectionHelper.unreflect;

public class ClassLoaderHelper {
    public static final MethodHandle DEFINE_CLASS = unreflect(() -> ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class));

    public static Class<?> defineClass(ClassLoader classLoader, String name, byte[] bytes) throws Throwable {
        return (Class<?>) DEFINE_CLASS.invoke(classLoader, name, bytes, 0, bytes.length);
    }

    public static boolean isClassLoaded(ClassLoader classLoader, String name) {
        try {
            Class.forName(name, false, classLoader);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
