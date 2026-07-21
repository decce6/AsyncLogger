package me.decce.asynclogger.mixins;

//? >1.12.2 {
import me.decce.transformingbase.core.LoggerConfigurator;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class ClientMainMixin {
    //? neoforge && <1.21.9 {
    /*@Inject(method = "main", at = @At("HEAD"))
    private static void asynclogger$main(String[] strings, CallbackInfo ci) {
        // On NeoForge we **already have** configured the logger; however, FML reconfigures the logger right before
        // entering this `main` method. Therefore, we need to configure the logger again.
        LoggerConfigurator.configure();
    }
    *///?}
}
//? }