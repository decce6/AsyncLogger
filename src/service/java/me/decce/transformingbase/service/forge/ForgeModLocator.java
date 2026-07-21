package me.decce.transformingbase.service.forge;

//? if forge {
/*//? if <=1.18.2 {
/^import me.decce.transformingbase.constants.Constants;
import net.minecraftforge.fml.loading.ModDirTransformerDiscoverer;
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

//? if 1.18.2 {
/^¹import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileDependencyLocator;
¹^///?} else {
import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileLocator;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileParser;
//?}

//? if 1.18.2 {
/^¹public class ForgeModLocator extends AbstractJarFileDependencyLocator {
¹^///?} else {
public class ForgeModLocator extends AbstractJarFileLocator {
 //?}
    @Override
    public List<IModFile> scanMods(
            //? 1.18.2
            /^¹Iterable<IModFile> loadedMods¹^/
    ) {
        List<IModFile> mods = new ArrayList<>();
        try {
            //? 1.18.2 {
            /^¹mods.add(createMod(getJarInJar()).get());
            ¹^///?} else {
            var jarPath = getJarPath();
            var modFile = new ModFile(jarPath, this, ModFileParser::modsTomlParser);
            this.modJars.put(modFile, createFileSystem(modFile));
            mods.add(modFile);
             //?}
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mods;
    }

    public Path getJarInJar() throws IOException, URISyntaxException {
        // From dev.kostromdan.mods.crash_assistant.common_config.loading_utils.JarInJarHelper
        //Idea taken from org.sinytra.connector.locator.EmbeddedDependencies#getJarInJar
        Path modPath = Paths.get(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
        Path pathInModFile = modPath.resolve(Constants.FORGE_JIJ_NAME);
        URI filePathUri = new URI("jij:" + pathInModFile.toAbsolutePath().toUri().getRawSchemeSpecificPart()).normalize();
        Map<String, Path> outerFsArgs = Collections.singletonMap("packagePath", pathInModFile);
        FileSystem zipFS = FileSystems.newFileSystem(filePathUri, outerFsArgs);
        return zipFS.getPath("/");
    }

    public static Path getJarPath() throws URISyntaxException {
        return Paths.get(ForgeModLocator.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    }

    //? <=1.16.5 {
    public static void inject() {
        try {
            var modPath = getJarPath();
            if (!ModDirTransformerDiscoverer.getExtraLocators().contains(modPath)) {
                ModDirTransformerDiscoverer.getExtraLocators().add(modPath);
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    //? }

    @Override
    public String name() {
        return Constants.MOD_ID;
    }

    @Override
    public void initArguments(Map<String, ?> arguments) {

    }
}
^///?} else >=1.16.5 {
import me.decce.transformingbase.constants.Constants;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModLocator;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ForgeModLocator implements IModLocator {
    @Override
    public List<ModFileOrException> scanMods() {
        return List.of();
    }

    @Override
    public String name() {
        return Constants.MOD_ID;
    }

    @Override
    public void scanFile(IModFile modFile, Consumer<Path> pathConsumer) {

    }

    @Override
    public void initArguments(Map<String, ?> arguments) {

    }

    @Override
    public boolean isValid(IModFile modFile) {
        return false;
    }
}
//?}
*///?}