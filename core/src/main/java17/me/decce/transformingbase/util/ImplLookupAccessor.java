package me.decce.transformingbase.util;

import sun.misc.Unsafe;
import sun.reflect.ReflectionFactory;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

public class ImplLookupAccessor {
    public static final MethodHandles.Lookup LOOKUP = getImplLookup();

    public static MethodHandles.Lookup getImplLookup() {
        try {
            var ctor = ReflectionFactory.getReflectionFactory()
                    .newConstructorForSerialization(MethodHandles.Lookup.class, MethodHandles.Lookup.class.getDeclaredConstructor(Class.class));
            var lookup = (MethodHandles.Lookup) ctor.newInstance(MethodHandles.Lookup.class);
            return (MethodHandles.Lookup) lookup.findStaticGetter(MethodHandles.Lookup.class, "IMPL_LOOKUP", MethodHandles.Lookup.class).invokeExact();
        }
        catch (Throwable throwable) {
            try {
                Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
                theUnsafe.setAccessible(true);
                Unsafe unsafe = (Unsafe) theUnsafe.get(null);
                Field implLookup = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
                return (MethodHandles.Lookup) unsafe.getObject(unsafe.staticFieldBase(implLookup), unsafe.staticFieldOffset(implLookup));
            }
            catch (Throwable throwable1) {
                throwable1.addSuppressed(throwable);
                throw new RuntimeException(throwable1);
            }
        }
    }
}
