package me.decce.transformingbase.util;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionHelper {
    public static <T> T unchecked(UncheckedSupplier<T, ?> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static MethodHandle unreflect(UncheckedSupplier<Method, ?> method) {
        try {
            return ImplLookupAccessor.LOOKUP.unreflect(method.get());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static MethodHandle unreflectOrNull(UncheckedSupplier<Method, ?> method) {
        try {
            var instance = method.get();
            if (instance == null) {
                return null;
            }
            return ImplLookupAccessor.LOOKUP.unreflect(instance);
        } catch (Throwable e) {
            return null;
        }
    }

    public static MethodHandle unreflectGetter(UncheckedSupplier<Field, ?> field) {
        try {
            return ImplLookupAccessor.LOOKUP.unreflectGetter(field.get());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    public interface UncheckedSupplier<T, E extends Exception> {
        T get() throws E;
    }
}
