package me.decce.transformingbase.transform;

import me.decce.transformingbase.util.ClassLoaderHelper;
import me.decce.transformingbase.util.ModuleHelper;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ClassLoaderHandler implements AutoCloseable {
    public final ClassLoader targetClassLoader;
    public final ClassLoader modClassLoader;

    public ClassLoaderHandler(ClassLoader targetClassLoader, ClassLoader modClassLoader) {
        this.targetClassLoader = targetClassLoader;
        this.modClassLoader = modClassLoader;
    }

    public static String toClassName(String name) {
        if (name.startsWith("/")) name = name.substring(1);
        return name.replace(".class", "").replace('/', '.');
    }

    public void loadCoreClasses(Class<?> modClass, String path) {
        int counter = 0;
        var throwable = new RuntimeException();
        try (var stream = getClassesStream(modClass, path)) {
            var classesToLoad = new LinkedList<>(stream.filter(p -> !Files.isDirectory(p) && p.toString().endsWith(".class")).collect(Collectors.toList()));
            while (!classesToLoad.isEmpty()) {
                var clazz = classesToLoad.remove(0);
                if (loadClass(clazz, throwable)) {
                    counter = 0;
                }
                else {
                    classesToLoad.add(clazz);
                    if (counter++ > classesToLoad.size()) {
                        throw throwable;
                    }
                }
            }
        }
    }

    protected Stream<Path> getClassesStream(Class<?> modClass, String path) {
        var resource = modClass.getResource(path);
        try {
            return walkResource(Objects.requireNonNull(resource).toURI());
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    protected Stream<Path> walkResource(URI resource) throws IOException {
        return Files.walk(Paths.get(resource));
    }

    private boolean loadClass(Path path, Throwable throwable) {
        try {
            var name = toClassName(path.toString());
            ClassLoaderHelper.defineClass(targetClassLoader, name, Files.readAllBytes(path));
            return true;
        }
        catch (NoClassDefFoundError e) {
            // Parent class not loaded yet - load the class later
            throwable.addSuppressed(e);
            return false;
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void expandModuleReads(Object... modules) {
        for (Object module : modules) {
            ModuleHelper.implAddReadsAllUnnamed(module);
        }
    }

    @Override
    public void close() {
    }

    public abstract void removeModClassesFromServiceLayer(String packageName);
}
