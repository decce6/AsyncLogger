package me.decce.transformingbase.service;

import me.decce.transformingbase.constants.Constants;
import me.decce.transformingbase.transform.ClassLoaderHandler;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;

import static me.decce.transformingbase.util.ReflectionHelper.unreflectGetter;

//? forge && <=1.16.5 {
/*import net.minecraftforge.fml.loading.FMLLoader;
*///?}
//? neoforge || (forge && >=1.18.2) {
/*import java.lang.module.ResolvedModule;
*///? }

public class ClassLoaderHandlerImpl extends ClassLoaderHandler {
    public ClassLoaderHandlerImpl(ClassLoader targetClassLoader, ClassLoader modClassLoader) {
        super(targetClassLoader, modClassLoader);
    }

    @Override
    public void removeModClassesFromServiceLayer(String packageName) {
        try {
            //? if neoforge && <1.21.9 {
            /*// At this point our classes are already loaded on the MC-BOOTSTRAP classloader, but we need to do this here
            // to prevent the LAYER SERVICE classloader from loading them again (out Mixin plugin needs to use them to
            // decide whether to apply mixins)
            var packageLookupGetter = unreflectGetter(() -> cpw.mods.cl.ModuleClassLoader.class.getDeclaredField("packageLookup"));
            var packageLookup = (Map<String, ResolvedModule>) packageLookupGetter.invoke(this.modClassLoader);
            packageLookup.entrySet().removeIf(e -> e.getKey().startsWith(packageName));

            // If we don't do this the LAYER SERVICE classloader will keep asking itself to load our class, eventually
            // causing a StackOverflowException
            var parentLoadersGetter = unreflectGetter(() -> cpw.mods.cl.ModuleClassLoader.class.getDeclaredField("parentLoaders"));
            var parentLoaders = (Map<String, ClassLoader>) parentLoadersGetter.invoke(this.modClassLoader);
            parentLoaders.entrySet().removeIf(e -> e.getKey().startsWith(packageName));
            *///?}
            //? if forge && >=1.21.1 {
            /*// At this point our classes are already loaded on the MC-BOOTSTRAP classloader, but we need to do this here
            // to prevent the TRANSFORMER classloader from loading them again (out Mixin plugin needs to use them to
            // decide whether to apply mixins)
            var packageToOurModulesGetter = unreflectGetter(() -> net.minecraftforge.securemodules.Securecpw.mods.cl.ModuleClassLoader.class.getDeclaredField("packageToOurModules"));
            var packageToOurModules = (Map<String, ResolvedModule>) packageToOurModulesGetter.invoke(this.modClassLoader);
            packageToOurModules.entrySet().removeIf(e -> e.getKey().startsWith(packageName));

            // If we don't do this the TRANSFORMER classloader will keep asking itself to load our class, eventually
            // causing a StackOverflowException
            var packageToParentLoaderGetter = unreflectGetter(() -> net.minecraftforge.securemodules.Securecpw.mods.cl.ModuleClassLoader.class.getDeclaredField("packageToParentLoader"));
            var packageToParentLoader = (Map<String, ClassLoader>) packageToParentLoaderGetter.invoke(this.modClassLoader);
            packageToParentLoader.entrySet().removeIf(e -> e.getKey().startsWith(packageName));
            *///?}
            //? if forge && <1.21.1 && >=1.18.2 {
            /*// At this point our classes are already loaded on the MC-BOOTSTRAP classloader, but we need to do this here
            // to prevent the LAYER SERVICE classloader from loading them again (out Mixin plugin needs to use them to
            // decide whether to apply mixins)
            var packageLookupGetter = unreflectGetter(() -> cpw.mods.cl.ModuleClassLoader.class.getDeclaredField("packageLookup"));
            var packageLookup = (Map<String, ResolvedModule>) packageLookupGetter.invoke(this.modClassLoader);
            packageLookup.entrySet().removeIf(e -> e.getKey().startsWith(packageName));

            // If we don't do this the LAYER SERVICE classloader will keep asking itself to load our class, eventually
            // causing a StackOverflowException
            var parentLoadersGetter = unreflectGetter(() -> cpw.mods.cl.ModuleClassLoader.class.getDeclaredField("parentLoaders"));
            var parentLoaders = (Map<String, ClassLoader>) parentLoadersGetter.invoke(this.modClassLoader);
            parentLoaders.entrySet().removeIf(e -> e.getKey().startsWith(packageName));
            *///?}
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    //? if forge && <=1.16.5 {
    /*private FileSystem fileSystem;

    @Override
    protected Stream<Path> getClassesStream(Class<?> modClass, String path) {
        var modPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        try {
            fileSystem = FileSystems.newFileSystem(Paths.get(modPath), (ClassLoader) null);
            return Files.walk(fileSystem.getPath(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        if (fileSystem != null) {
            try {
                fileSystem.close();
            } catch (IOException ignored) {
            }
        }
    }
    *///?}

    //? if (neoforge && >=1.21.9) || fabric {
    private FileSystem fileSystem;

    @Override
    protected Stream<Path> walkResource(URI resource) throws IOException {
        var s = resource.toString().split("!");
        fileSystem = FileSystems.newFileSystem(URI.create(s[0]), Map.of());
        var path = fileSystem.getPath(s[1]);
        return Files.walk(path);
    }

    @Override
    public void close() {
        if (fileSystem != null) {
            try {
                fileSystem.close();
            } catch (IOException ignored) {
            }
        }
    }
    //?}
}
