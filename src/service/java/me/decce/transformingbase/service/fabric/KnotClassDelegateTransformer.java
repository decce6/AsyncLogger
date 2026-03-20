package me.decce.transformingbase.service.fabric;

import me.decce.transformingbase.constants.Constants;
import net.lenni0451.classtransform.annotations.CInline;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CRedirect;

@CTransformer(name = "net.fabricmc.loader.impl.launch.knot.KnotClassDelegate")
public class KnotClassDelegateTransformer {
    @CInline
    @CRedirect(method = "tryLoadClass", target = @CTarget(value = "INVOKE", target = "Ljava/lang/String;startsWith(Ljava/lang/String;)Z", ordinal = 0))
    private boolean filterLoadClass(String name, String original) {
        return name.startsWith(original) || name.startsWith(Constants.CORE_PACKAGE);
    }
}
