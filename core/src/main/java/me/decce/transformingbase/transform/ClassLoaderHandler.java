package me.decce.transformingbase.transform;

import me.decce.transformingbase.util.ClassLoaderHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class ClassLoaderHandler implements AutoCloseable {
    public final ClassLoader targetClassLoader;
    public final ClassLoader modClassLoader;

    protected final Logger LOGGER = LogManager.getLogger();

    public ClassLoaderHandler(ClassLoader targetClassLoader, ClassLoader modClassLoader) {
        this.targetClassLoader = targetClassLoader;
        this.modClassLoader = modClassLoader;
    }

    public static String toClassName(String name) {
        if (name.startsWith("/")) name = name.substring(1);
        return name.replace(".class", "").replace('/', '.');
    }

    public void loadCoreClasses(Class<?> modClass) {
        int counter = 0;
        var throwable = new RuntimeException();
        try (var stream = getClassesStream(modClass)) {
            var classesToLoad = new LinkedList<>(stream.filter(p -> !Files.isDirectory(p) && p.toString().endsWith(".class")).toList());
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

    protected Stream<Path> getClassesStream(Class<?> modClass) {
        var resource = modClass.getResource("/me/decce/transformingbase/core");
        try {
            return walkResource(Objects.requireNonNull(resource).toURI());
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    protected Stream<Path> walkResource(URI resource) throws IOException {
        return Files.walk(Path.of(resource));
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

    @Override
    public void close() {
    }

    public abstract void removeModClassesFromServiceLayer();
}
