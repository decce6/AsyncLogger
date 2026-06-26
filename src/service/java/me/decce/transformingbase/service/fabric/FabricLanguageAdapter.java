package me.decce.transformingbase.service.fabric;

//? fabric {
import me.decce.transformingbase.service.Bootstrapper;
import net.fabricmc.loader.api.LanguageAdapter;
import net.fabricmc.loader.api.ModContainer;

// Note: LanguageAdapter seems to be the earliest available entrypoint on Fabric, which allows mods in PreLaunch to
// benefit from asynchronous logging and be filtered
// TODO: find a way to ensure we run *after* CrashAssistant
@SuppressWarnings("unused")
public class FabricLanguageAdapter implements LanguageAdapter {
    public FabricLanguageAdapter() {
        Bootstrapper.bootstrap();
    }

    @Override
    public <T> T create(ModContainer mod, String value, Class<T> type) {
        throw new RuntimeException();
    }
}
//?}
