package me.decce.transformingbase.service.forge;

//? if forge {
/*import me.decce.transformingbase.constants.Constants;
import me.decce.transformingbase.service.Bootstrapper;
import net.minecraftforge.fml.loading.ImmediateWindowProvider;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class ForgeImmediateWindowProvider implements ImmediateWindowProvider {
    public ForgeImmediateWindowProvider() {
        Bootstrapper.bootstrap();
    }

    @Override
    public String name() {
        return Constants.MOD_ID;
    }

    @Override
    public Runnable initialize(String[] arguments) {
        throw new InvalidImmediateWindowProviderException();
    }

    @Override
    public void updateFramebufferSize(IntConsumer width, IntConsumer height) {
        throw new InvalidImmediateWindowProviderException();
    }

    @Override
    public long setupMinecraftWindow(IntSupplier width, IntSupplier height, Supplier<String> title, LongSupplier monitor) {
        throw new InvalidImmediateWindowProviderException();
    }

    @Override
    public boolean positionWindow(Optional<Object> monitor, IntConsumer widthSetter, IntConsumer heightSetter, IntConsumer xSetter, IntConsumer ySetter) {
        throw new InvalidImmediateWindowProviderException();
    }

    @Override
    public <T> Supplier<T> loadingOverlay(Supplier<?> mc, Supplier<?> ri, Consumer<Optional<Throwable>> ex, boolean fade) {
        throw new InvalidImmediateWindowProviderException();
    }

    @Override
    public void updateModuleReads(ModuleLayer layer) {
        throw new InvalidImmediateWindowProviderException();
    }

    @Override
    public void periodicTick() {
        throw new InvalidImmediateWindowProviderException();
    }

    @Override
    public String getGLVersion() {
        throw new InvalidImmediateWindowProviderException();
    }
}
*///?}
