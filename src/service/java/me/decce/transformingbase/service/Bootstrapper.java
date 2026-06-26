package me.decce.transformingbase.service;

import me.decce.transformingbase.constants.Constants;
import me.decce.transformingbase.util.ClassLoaderHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bootstrapper {
    private static boolean bootstrapped;
    private static boolean skippedDisruptorLoading;

    public static void bootstrap() {
        if (bootstrapped) {
            return;
        }
        bootstrapped = true;

        try (var classLoaderHandler = new ClassLoaderHandlerImpl(Logger.class.getClassLoader(), Bootstrapper.class.getClassLoader())) {
            //? if !fabric {
            /*classLoaderHandler.expandModuleReads(Logger.class.getModule(), org.apache.logging.log4j.core.Logger.class.getModule());
            if (ClassLoaderHelper.isClassLoaded(classLoaderHandler.targetClassLoader, "com.lmax.disruptor.RingBuffer")) {
                skippedDisruptorLoading = true;
            } else {
                classLoaderHandler.loadCoreClasses(Bootstrapper.class, "/com/lmax");
            }
            classLoaderHandler.removeModClassesFromServiceLayer("com.lmax");
             *///?}
        }

        initConfig();

        if (AsyncLogger.config.enabled) {
            if (AsyncLogger.config.filtering) {
                AsyncLogger.compileFilters();
            }
            LoggerConfigurator.configure();
        }

        if (skippedDisruptorLoading) {
            LogManager.getLogger(Constants.MOD_ID).info("Note: skipped loading disruptor because it was already present on classpath");
        }
    }

    private static void initConfig() {
        AsyncLogger.config = ConfigLoader.load();
        ConfigLoader.save(AsyncLogger.config);
    }
}
