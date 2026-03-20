package me.decce.transformingbase.util;

import net.lenni0451.reflect.JavaBypass;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.security.ProtectionDomain;
import java.util.Objects;

import static me.decce.transformingbase.util.ReflectionHelper.unreflect;

public class ClassLoaderHelper {
    public static final MethodHandle FIND_LOADED_CLASS = unreflect(() -> ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class));
    public static final Object INTERNAL_UNSAFE = JavaBypass.getInternalUnsafe();
    public static final MethodHandle DEFINE_CLASS_INTERNAL = unreflect(() -> INTERNAL_UNSAFE.getClass().getDeclaredMethod("defineClass",
            String.class, byte[].class, int.class, int.class,
            ClassLoader.class, ProtectionDomain.class));

    public static boolean isClassLoaded(ClassLoader classLoader, String clazz) {
        try {
            if (classLoader == null) {
                return true;
            }
            return (Class<?>) FIND_LOADED_CLASS.invokeExact(classLoader, clazz) != null;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?> defineClass(ClassLoader classLoader, String name, byte[] bytes) throws Throwable {
        return (Class<?>) DEFINE_CLASS_INTERNAL.invoke(INTERNAL_UNSAFE, name, bytes, 0, bytes.length, classLoader, null);
    }

    public static byte[] classToBytes(Class<?> clazz) throws IOException {
        String name = "/" + clazz.getName().replace('.', '/') + ".class";
        return Objects.requireNonNull(clazz.getResourceAsStream(name)).readAllBytes();
    }
}
