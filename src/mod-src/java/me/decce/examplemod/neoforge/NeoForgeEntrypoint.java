package me.decce.examplemod.neoforge;

//? if neoforge {
import me.decce.examplemod.ExampleMod;
import me.decce.transformingbase.constants.Constants;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class NeoForgeEntrypoint {
    public NeoForgeEntrypoint() {
        ExampleMod.init();
    }
}
//?}
