package me.decce.transformingbase.transform;

import me.decce.transformingbase.constants.Constants;
import me.decce.transformingbase.util.ClassLoaderHelper;
import net.lenni0451.classtransform.TransformerManager;
import net.lenni0451.classtransform.utils.tree.BasicClassProvider;

import java.lang.instrument.Instrumentation;
import java.lang.invoke.MethodHandle;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.decce.transformingbase.util.ReflectionHelper.unreflect;

public class TransformationHelper {
    public final MethodHandle IMPL_ADD_READS_ALL_UNNAMED = unreflect(() -> Module.class.getDeclaredMethod("implAddReadsAllUnnamed"));

    public final ClassLoader targetClassLoader;
    public final ClassLoader modClassLoader;

    public TransformationHelper(ClassLoader targetClassLoader, ClassLoader modClassLoader) {
        this.targetClassLoader = targetClassLoader;
        this.modClassLoader = modClassLoader;
    }

    public void expandModuleReads(Module... modules) {
        try {
            for (Module module : modules) {
                IMPL_ADD_READS_ALL_UNNAMED.invokeExact(module);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void setup(Instrumentation instrumentation, boolean retransform, boolean setupNative, TransformerDefinition... additionalTransformers) {
        var transformers = Stream.concat(Arrays.stream(TransformerDefinitions.values()).map(td -> td.definition), Arrays.stream(additionalTransformers)).toList();
        preSetup(transformers, retransform);

        var manager = getTransformerManager(transformers.stream().map(t -> t.transformer).collect(Collectors.toSet()));
        instrumentation.addTransformer(manager, retransform);

        postSetup(transformers, manager, instrumentation, retransform, setupNative);
    }

    private void preSetup(List<TransformerDefinition> transformers, boolean retransform) {
        for (var transformer : transformers) {
            if (!retransform && ClassLoaderHelper.isClassLoaded(this.targetClassLoader, transformer.target)) {
                throw new RuntimeException("Class " + transformer.target + " was loaded too early.");
            }
        }
    }

    private void postSetup(List<TransformerDefinition> transformers, TransformerManager manager, Instrumentation instrumentation, boolean retransform, boolean setupNative) {
        if (setupNative) {
            if (retransform) {
                throw new RuntimeException("Cannot retransform class for native methods");
            }
            instrumentation.setNativeMethodPrefix(manager, Constants.NATIVE_METHOD_PREFIX);
        }
        if (retransform) {
            try {
                Set<Class<?>> targets = new HashSet<>();
                for (var transformer : transformers) {
                    targets.add(Class.forName(transformer.target));
                }
                instrumentation.retransformClasses(targets.toArray(new Class[0]));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected TransformerManager getTransformerManager(Iterable<Class<?>> transformers) {
        var provider = new BasicClassProvider(modClassLoader);
        var manager = new TransformerManager(provider);
        for (Class<?> transformer : transformers) {
            manager.addTransformer(transformer.getName());
        }
        return manager;
    }
}
