package me.decce.transformingbase.util;

import java.lang.invoke.MethodHandles;

public class ImplLookupAccessor {
    public static final MethodHandles.Lookup LOOKUP = getImplLookup();

    public static MethodHandles.Lookup getImplLookup() {
        try {
            var field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            field.setAccessible(true);
            return (MethodHandles.Lookup) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
