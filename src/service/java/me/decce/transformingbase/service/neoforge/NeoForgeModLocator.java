package me.decce.transformingbase.service.neoforge;

//? if neoforge {
import me.decce.transformingbase.constants.Constants;
import net.neoforged.neoforgespi.ILaunchContext;
import net.neoforged.neoforgespi.locating.IDiscoveryPipeline;
import net.neoforged.neoforgespi.locating.IModFileCandidateLocator;
import net.neoforged.neoforgespi.locating.IncompatibleFileReporting;
import net.neoforged.neoforgespi.locating.ModFileDiscoveryAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class NeoForgeModLocator implements IModFileCandidateLocator {
    @Override
    public void findCandidates(ILaunchContext context, IDiscoveryPipeline pipeline) {
        //? if <1.21.9 {
        /*try {
            final var resource = this.getClass().getResource("/META-INF/jarjar/");
            try (var stream = Files.walk(Path.of(Objects.requireNonNull(resource).toURI()), 1)) {
                stream.filter(path -> path.getFileName().toString().endsWith("-mod.jar"))
                        .forEach(path -> pipeline.addJarContent(cpw.mods.jarhandling.JarContents.of(path), ModFileDiscoveryAttributes.DEFAULT, IncompatibleFileReporting.ERROR));
            }
        } catch (Throwable t) {
            throw new RuntimeException("Loading " + Constants.MOD_ID + " JiJ mod", t);
        }
        *///?}
    }
}
//?}
