package me.decce.transformingbase.transform;

public class TransformerDefinition {
    public final Class<?> transformer;
    public final String target;

    public TransformerDefinition(String target, Class<?> transformer) {
        this.transformer = transformer;
        this.target = target;
    }

    public static TransformerDefinition of(String target, Class<?> transformer) {
        return new TransformerDefinition(target, transformer);
    }
}
