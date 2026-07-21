package me.decce.transformingbase.util;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationTargetException;

import static me.decce.transformingbase.util.ReflectionHelper.unreflectOrNull;

public class ModuleHelper {
    public static final MethodHandle IMPL_ADD_READS_ALL_UNNAMED = unreflectOrNull(() -> Class.forName("java.lang.Module").getDeclaredMethod("implAddReadsAllUnnamed"));

    public static Object getModuleOfClass(Class<?> clazz) {
        if (JavaHelper.major() >= 9) {
            try {
                return Class.class.getMethod("getModule").invoke(clazz);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static void implAddReadsAllUnnamed(Object module) {
        if (JavaHelper.major() >= 9) {
            try {
                IMPL_ADD_READS_ALL_UNNAMED.invoke(module);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        // No-op on Java 8
    }
}
