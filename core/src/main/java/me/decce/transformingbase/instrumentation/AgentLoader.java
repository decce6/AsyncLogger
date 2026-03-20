package me.decce.transformingbase.instrumentation;

import me.decce.transformingbase.util.ClassLoaderHelper;

import java.lang.instrument.Instrumentation;
import java.lang.invoke.MethodHandle;
import java.util.Objects;

import static me.decce.transformingbase.util.ReflectionHelper.unchecked;
import static me.decce.transformingbase.util.ReflectionHelper.unreflect;

public class AgentLoader {
    private static final ClassLoader CLASSLOADER = ClassLoader.getSystemClassLoader();
    private static final String NAME = AgentMain.class.getName();
    private static Class<AgentMain> AGENT_MAIN_CLASS;
    private static final Class<?> INSTRUMENTATION_IMPL = unchecked(() -> Class.forName("sun.instrument.InstrumentationImpl"));
    private static final MethodHandle LOAD_AGENT = unreflect(() -> INSTRUMENTATION_IMPL.getDeclaredMethod("loadAgent", String.class));

    static {
        try {
            if (!ClassLoaderHelper.isClassLoaded(CLASSLOADER, NAME)) {
                AGENT_MAIN_CLASS = (Class<AgentMain>) ClassLoaderHelper.defineClass(CLASSLOADER, NAME, ClassLoaderHelper.classToBytes(AgentMain.class));
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Instrumentation load(String path) {
        var found = tryFind();
        if (found != null) {
            return found;
        }
        try {
            LOAD_AGENT.invokeExact(path);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return Objects.requireNonNull(tryFind());
    }

    private static Instrumentation tryFind() {
        try {
            return (Instrumentation) AGENT_MAIN_CLASS.getField("instrumentation").get(null);
        } catch (Exception e) {
            return null;
        }
    }

}
