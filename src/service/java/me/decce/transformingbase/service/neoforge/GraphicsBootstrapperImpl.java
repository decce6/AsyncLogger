package me.decce.transformingbase.service.neoforge;

//? if neoforge {
import me.decce.transformingbase.constants.Constants;
import me.decce.transformingbase.service.Bootstrapper;
import net.neoforged.neoforgespi.earlywindow.GraphicsBootstrapper;

public class GraphicsBootstrapperImpl implements GraphicsBootstrapper {
    @Override
    public String name() {
        return Constants.MOD_ID;
    }

    @Override
    public void bootstrap(String[] arguments) {
        Bootstrapper.bootstrap();
    }
}
//?}
