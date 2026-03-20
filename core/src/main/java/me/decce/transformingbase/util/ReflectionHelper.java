package me.decce.transformingbase.util;

import net.lenni0451.reflect.JavaBypass;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionHelper {
    public static final MethodHandles.Lookup LOOKUP = JavaBypass.TRUSTED_LOOKUP;

    public static <T> T unchecked(UncheckedSupplier<T, ?> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static MethodHandle unreflect(UncheckedSupplier<Method, ?> method) {
        try {
            return LOOKUP.unreflect(method.get());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static MethodHandle unreflectGetter(UncheckedSupplier<Field, ?> field) {
        try {
            return LOOKUP.unreflectGetter(field.get());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    public interface UncheckedSupplier<T, E extends Exception> {
        T get() throws E;
    }
}
