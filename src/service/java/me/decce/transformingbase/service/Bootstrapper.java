package me.decce.transformingbase.service;

import me.decce.transformingbase.constants.Constants;
import me.decce.transformingbase.core.ExampleCore;
import me.decce.transformingbase.core.LibraryAccessor;
import me.decce.transformingbase.instrumentation.AgentLoader;
import me.decce.transformingbase.transform.TransformationHelper;
import me.decce.transformingbase.transform.TransformerDefinition;
import net.lenni0451.reflect.Agents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFWErrorCallbackI;

import java.lang.instrument.Instrumentation;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.Objects;

import static me.decce.transformingbase.util.ReflectionHelper.unreflect;

public class Bootstrapper {
    private static final Logger LOGGER = LogManager.getLogger(Constants.MOD_NAME);
    private static boolean bootstrapped;

    public static void bootstrap() {
        if (bootstrapped) {
            return;
        }
        bootstrapped = true;

        var classLoaderHandler = new ClassLoaderHandlerImpl(GLFWErrorCallbackI.class.getClassLoader(), Bootstrapper.class.getClassLoader());
        classLoaderHandler.loadCoreClasses(Bootstrapper.class);
        classLoaderHandler.removeModClassesFromServiceLayer();

        var helper = new TransformationHelper(classLoaderHandler.targetClassLoader, classLoaderHandler.modClassLoader);

        var openglModule = org.lwjgl.opengl.AMDPinnedMemory.class.getModule();
        helper.expandModuleReads(openglModule);

        helper.setup(getInstrumentation(), false, true
                //? if fabric {
                /*, new TransformerDefinition("net.fabricmc.loader.impl.launch.knot.KnotClassDelegate", me.decce.transformingbase.service.fabric.KnotClassDelegateTransformer.class)
                *///?}
        );

        classLoaderHandler.close();

        initMethodHandles();
        initConfig();
    }

    private static Instrumentation getInstrumentation() {
        try {
            var codeSource = Objects.requireNonNull(Bootstrapper.class.getProtectionDomain().getCodeSource());
            var path = getAgentPath(codeSource);
            if (path != null) {
                return AgentLoader.load(path);
            }
            else {
                LOGGER.warn("Could not find mod jar, using fallback method for instrumentation");
                return Agents.getInstrumentation();
            }
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static String getAgentPath(CodeSource codeSource) {
        var location = codeSource.getLocation();
        try {
            return Paths.get(location.toURI()).toFile().getAbsolutePath();
        } catch (UnsupportedOperationException | URISyntaxException uoe) {
            if ("union".equals(location.getProtocol())) {
                var path = location.getPath();
                if (path.contains("%")) {
                    String parsed = path.substring(0, path.indexOf('%'));
                    if (Files.exists(Paths.get(parsed))) {
                        return parsed;
                    }
                }
            }
            return null;
        }
    }

    private static void initConfig() {
        ExampleCore.config = ConfigLoader.load();
        ConfigLoader.save(ExampleCore.config);
    }

    private static void initMethodHandles() {
        LibraryAccessor.logger = LOGGER;
        LibraryAccessor.logInfoString = unreflect(() -> Logger.class.getMethod("info", String.class));
        LibraryAccessor.logInfoObject = unreflect(() -> Logger.class.getMethod("info", Object.class));
        LibraryAccessor.logInfoStringObject = unreflect(() -> Logger.class.getMethod("info", String.class, Object.class));
        LibraryAccessor.logWarnString = unreflect(() -> Logger.class.getMethod("warn", String.class));
        LibraryAccessor.logWarnObject = unreflect(() -> Logger.class.getMethod("warn", Object.class));
        LibraryAccessor.logWarnStringObject = unreflect(() -> Logger.class.getMethod("warn", String.class, Object.class));
        LibraryAccessor.logErrorString = unreflect(() -> Logger.class.getMethod("error", String.class));
        LibraryAccessor.logErrorObject = unreflect(() -> Logger.class.getMethod("error", Object.class));
        LibraryAccessor.logErrorStringObject = unreflect(() -> Logger.class.getMethod("error", String.class, Object.class));
        LibraryAccessor.logFatalString = unreflect(() -> Logger.class.getMethod("fatal", String.class));
        LibraryAccessor.logFatalObject = unreflect(() -> Logger.class.getMethod("fatal", Object.class));
        LibraryAccessor.logFatalStringObject = unreflect(() -> Logger.class.getMethod("fatal", String.class, Object.class));
        LibraryAccessor.logDebugString = unreflect(() -> Logger.class.getMethod("debug", String.class));
        LibraryAccessor.logDebugObject = unreflect(() -> Logger.class.getMethod("debug", Object.class));
        LibraryAccessor.logDebugStringObject = unreflect(() -> Logger.class.getMethod("debug", String.class, Object.class));
    }
}
