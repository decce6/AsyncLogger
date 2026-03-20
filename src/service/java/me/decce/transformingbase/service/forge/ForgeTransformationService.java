package me.decce.transformingbase.service.forge;

//? if forge || (neoforge && <1.21.9) {
/*import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import me.decce.transformingbase.constants.Constants;
import me.decce.transformingbase.service.Bootstrapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class ForgeTransformationService implements ITransformationService {
    @Override
    public @NotNull String name() {
        return Constants.MOD_ID;
    }

    @Override
    public void initialize(IEnvironment environment) {
    //? if forge {
        /^// Required when earlyWindowControl is disabled
        Bootstrapper.bootstrap();
    ^///?}
    }

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) throws IncompatibleEnvironmentException {

    }

    //? if forge {
    /^@Override
    public @NotNull List<ITransformer> transformers() {
        return List.of();
    }
    ^///?} else {
    @Override
    public @NotNull List<? extends ITransformer<?>> transformers() {
        return List.of();
    }
    //?}
}
*///?}
