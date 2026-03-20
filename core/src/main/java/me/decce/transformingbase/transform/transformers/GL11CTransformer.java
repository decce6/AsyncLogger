package me.decce.transformingbase.transform.transformers;

import me.decce.transformingbase.core.LibraryAccessor;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.COverride;
import org.lwjgl.opengl.GL11C;

@CTransformer(GL11C.class)
public class GL11CTransformer {
    public static native long examplemod_wrapped$nglGetString(int var0);

    @COverride
    public static long nglGetString(int var0) {
        LibraryAccessor.info("Example Transformer - glGetString({})", var0);
        return examplemod_wrapped$nglGetString(var0);
    }

}
