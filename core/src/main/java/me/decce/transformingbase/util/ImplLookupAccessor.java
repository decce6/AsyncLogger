package me.decce.transformingbase.util;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class ImplLookupAccessor {
    public static final MethodHandles.Lookup LOOKUP = getImplLookup();

    public static MethodHandles.Lookup getImplLookup() {
        if (JavaHelper.major() <= 8) {
            return getImplLookupJava8();
        }
        else {
            return getImplLookupJavaLatest();
        }
    }

    private static MethodHandles.Lookup getImplLookupJava8() {
        try {
            var field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            field.setAccessible(true);
            return (MethodHandles.Lookup) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static MethodHandles.Lookup getImplLookupJavaLatest() {
        try {
            var clazzReflectionFactory = Class.forName("sun.reflect.ReflectionFactory");
            var reflectionFactory = clazzReflectionFactory.getMethod("getReflectionFactory").invoke(null);
            @SuppressWarnings("unchecked") var ctor = (Constructor<MethodHandles.Lookup>) reflectionFactory.getClass().getMethod("newConstructorForSerialization", Class.class, Constructor.class).invoke(reflectionFactory, MethodHandles.Lookup.class, MethodHandles.Lookup.class.getDeclaredConstructor(Class.class));
            var lookup = ctor.newInstance(MethodHandles.Lookup.class);
            return (MethodHandles.Lookup) lookup.findStaticGetter(MethodHandles.Lookup.class, "IMPL_LOOKUP", MethodHandles.Lookup.class).invokeExact();
        }
        catch (Throwable throwable) {
            try {
                var clazzUnsafe = Class.forName("sun.misc.Unsafe");
                Field theUnsafe = clazzUnsafe.getDeclaredField("theUnsafe");
                theUnsafe.setAccessible(true);
                Object unsafe = theUnsafe.get(null);
                Field implLookup = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
                var methodGetObject = clazzUnsafe.getMethod("getObject", Object.class, long.class);
                var methodStaticFieldBase = clazzUnsafe.getMethod("staticFieldBase", Field.class);
                var methodStaticFieldOffset = clazzUnsafe.getMethod("staticFieldOffset", Field.class);
                return (MethodHandles.Lookup) methodGetObject.invoke(unsafe, methodStaticFieldBase.invoke(unsafe, implLookup), (long) methodStaticFieldOffset.invoke(unsafe, implLookup));
            }
            catch (Throwable throwable1) {
                throwable1.addSuppressed(throwable);
                throw new RuntimeException(throwable1);
            }
        }
    }
}
