package me.decce.transformingbase.transform;

import me.decce.transformingbase.transform.transformers.GL11CTransformer;

public enum TransformerDefinitions {
    GL11C("org.lwjgl.opengl.GL11C", GL11CTransformer.class);

    public final TransformerDefinition definition;

    TransformerDefinitions(String target, Class<?> transformer) {
        this(new TransformerDefinition(target, transformer));
    }

    TransformerDefinitions(TransformerDefinition definition) {
        this.definition = definition;
    }
}
