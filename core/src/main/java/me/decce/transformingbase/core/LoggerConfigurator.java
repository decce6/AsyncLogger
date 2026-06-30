package me.decce.transformingbase.core;

import me.decce.transformingbase.constants.Constants;
import me.decce.transformingbase.core.sysout.FilteringPrintStream;
import me.decce.transformingbase.core.sysout.RedirectingPrintStream;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.async.BasicAsyncLoggerContextSelector;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.impl.Log4jContextFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class LoggerConfigurator {
    private static boolean configured;

    static {
        var config = AsyncLogger.config;
        if (config.ringBufferSize > 0) {
            System.setProperty("log4j2.asyncLoggerRingBufferSize", String.valueOf(config.ringBufferSize));
        }
        else if (config.ringBufferSize == 0) {
            // When testing performance we're printing a large volume in a very short period, which almost never happens in actual gameplay
            // We need to specify a large enough buffer size to avoid throtting.
            System.setProperty("log4j2.asyncLoggerRingBufferSize", String.valueOf(config.testPerformance ? 256 * 1024 : 8 * 1024));
        }
        if (!config.waitStrategy.isEmpty()) {
            System.setProperty("log4j2.asyncLoggerWaitStrategy", config.waitStrategy);
        }
        if (!config.synchronizeEnqueueWhenQueueFull.isEmpty()) {
            System.setProperty("log4j2.asyncLoggerSynchronizeEnqueueWhenQueueFull", config.synchronizeEnqueueWhenQueueFull);
        }
        if (!config.formatMsgAsync.isEmpty()) {
            System.setProperty("log4j2.formatMsgAsync", config.formatMsgAsync);
        }
        if (!config.asyncQueueFullPolicy.isEmpty()) {
            System.setProperty("log4j2.asyncQueueFullPolicy", config.asyncQueueFullPolicy);
        }
        if (!config.discardThreshold.isEmpty()) {
            System.setProperty("log4j2.discardThreshold", config.discardThreshold);
        }
    }

    public static void configure() {
        var test = AsyncLogger.config.testPerformance;
        List<LoggerTester.Result> before = null;
        List<LoggerTester.Result> after = null;
        if (test) {
            before = LoggerTester.testAll();
        }

        // Note: if logger was created before this, the original would still be used even after we configure the new
        // async logger factory. Therefore, we configure both the original root logger and the async one.
        // An example of what this fixes is the "Exiting event polling thread" message from Ixeris.
        configureRootLogger();
        var configuration = LoggerContext.getContext(false).getConfiguration();
        var originalRoot = configuration.getRootLogger();
        var originalAppenders = Map.copyOf(originalRoot.getAppenders());
        var originalAppenderRefs = List.copyOf(originalRoot.getAppenderRefs());

        var selector = new BasicAsyncLoggerContextSelector();
        LogManager.setFactory(new Log4jContextFactory(selector));

        configureRootLogger();

        var root = configuration.getRootLogger();
        for (var entry : originalAppenders.entrySet()) {
            if (!root.getAppenders().containsKey(entry.getKey())) {
                var level = Level.ALL;
                var filter = (Filter) null;
                for (var ref : originalAppenderRefs) {
                    if (ref.getRef().equals(entry.getKey())) {
                        level = ref.getLevel() != null ? ref.getLevel() : Level.ALL;
                        filter = ref.getFilter();
                        break;
                    }
                }
                root.addAppender(entry.getValue(), level, filter);
            }
        }

        configureSysOutErr();

        if (!configured) {
            // On Neo 1.21.1 we have to configure the logger twice (see MainMixin), but we only print the message
            // and do the test once
            configured = true;

            var logger = LogManager.getLogger(Constants.MOD_NAME);
            logger.info("Successfully configured async logger context with [wrapSysOutSysErr={}, filtering.enabled={}, filtering.global={}]", AsyncLogger.config.wrapSysOutSysErr, AsyncLogger.config.filtering, AsyncLogger.config.filterGlobal);

            if (test) {
                after = LoggerTester.testAll();
                logger.info("--- Test Results before applying AsyncLogger ---");
                for (var result : before) {
                    logger.info("{}: {}ms", result.item(), result.elapsedTimeInMs());
                }
                logger.info("--- Test Results after applying AsyncLogger ---");
                for (var result : after) {
                    logger.info("{}: {}ms", result.item(), result.elapsedTimeInMs());
                }
            }
        }
    }

    private static void configureRootLogger() {
        var root = LoggerContext.getContext(false).getConfiguration().getRootLogger();
        configureDebugLog(root);
        configureFilter();
    }

    private static void configureDebugLog(LoggerConfig loggerConfig) {
        if (AsyncLogger.config.noDebugLog) {
            String name = "DebugFile";
            if (loggerConfig.getAppenders().containsKey(name)) {
                loggerConfig.getAppenders().get(name).stop();
                loggerConfig.removeAppender(name);
            }
            try {
                // Attempt to delete the already-generated debug log file
                // This prevents logs from building up and hence multiple archives of old debug logs being created
                Files.delete(Paths.get("logs", "debug.log"));
            } catch (IOException ignored) {}

            // Delete debug log archives created by crash before AsyncLogger initializes
            // Early crash still produce a debug log, which will archived when another early crash happens
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("logs"), "debug-[1-5].log.gz")) {
                for (Path file : stream) {
                    Files.delete(file);
                }
            } catch (IOException ignored) {}
        }
    }

    private static void configureFilter() {
        if (AsyncLogger.config.filtering) {
            // https://logging.apache.org/log4j/2.x/manual/filters.html#filtering-process
            if (AsyncLogger.config.filterGlobal) {
                // Filter in the "Logger" stage (earliest stage)
                LoggerContext.getContext(false).addFilter(new AsyncFilter(AsyncLogger.filteringInfo));
            }
            else {
                // Filter in the "LoggerConfig" stage (2nd stage, earliest after LogEvent creation)
                ((Logger) LogManager.getRootLogger()).addFilter(new AsyncFilter(AsyncLogger.filteringInfo));
            }
        }
    }

    private static void configureSysOutErr() {
        if (AsyncLogger.config.wrapSysOutSysErr) {
            if (!(System.out instanceof RedirectingPrintStream)) {
                System.setOut(new RedirectingPrintStream("STDOUT", System.out));
                System.setErr(new RedirectingPrintStream("STDERR", System.err));
            }
        } else if (AsyncLogger.config.filtering && AsyncLogger.config.filterSysOut) {
            if (!(System.out instanceof FilteringPrintStream)) {
                System.setOut(new FilteringPrintStream(System.out, AsyncLogger.filteringInfo, Level.INFO));
                System.setErr(new FilteringPrintStream(System.err, AsyncLogger.filteringInfo, Level.ERROR));
            }
        }
    }
}
