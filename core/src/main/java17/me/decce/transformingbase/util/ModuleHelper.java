package me.decce.transformingbase.util;

import java.lang.invoke.MethodHandle;

import static me.decce.transformingbase.util.ReflectionHelper.unreflect;

public class ModuleHelper {
    public static final MethodHandle IMPL_ADD_READS_ALL_UNNAMED = unreflect(() -> Module.class.getDeclaredMethod("implAddReadsAllUnnamed"));

    public static Object getModuleOfClass(Class<?> clazz) {
        return clazz.getModule();
    }

    public static void implAddReadsAllUnnamed(Object module) {
        try {
            IMPL_ADD_READS_ALL_UNNAMED.invoke(module);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
