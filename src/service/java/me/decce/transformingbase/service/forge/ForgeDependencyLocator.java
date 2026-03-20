package me.decce.transformingbase.service.forge;

//? if forge {
/*import me.decce.transformingbase.constants.Constants;
import net.minecraftforge.fml.loading.moddiscovery.JarInJarDependencyLocator;
import net.minecraftforge.forgespi.locating.IModFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ForgeDependencyLocator extends JarInJarDependencyLocator {
    @Override
    public List<IModFile> scanMods(Iterable<IModFile> loadedMods) {
        List<IModFile> mods = new ArrayList<>();
        try {
            mods.add(createMod(getJarInJar()).file());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mods;
    }

    public Path getJarInJar() throws IOException, URISyntaxException {
        // From dev.kostromdan.mods.crash_assistant.common_config.loading_utils.JarInJarHelper
        //Idea taken from org.sinytra.connector.locator.EmbeddedDependencies#getJarInJar
        Path pathInModFile = Paths.get(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).resolve(Constants.FORGE_JIJ_NAME);
        URI filePathUri = new URI("jij:" + pathInModFile.toAbsolutePath().toUri().getRawSchemeSpecificPart()).normalize();
        Map<String, Path> outerFsArgs = Collections.singletonMap("packagePath", pathInModFile);
        FileSystem zipFS = FileSystems.newFileSystem(filePathUri, outerFsArgs);
        return zipFS.getPath("/");
    }

    @Override
    public String name() {
        return Constants.MOD_ID;
    }
}
*///?}