package me.decce.transformingbase.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "unused"})
public class ExampleModConfig {
    @Comment("Specifies whether to enable the mod")
    public boolean enabled = true;

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Comment {
        String value();
    }
}
