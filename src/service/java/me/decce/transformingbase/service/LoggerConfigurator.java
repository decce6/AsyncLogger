package me.decce.transformingbase.service;

import me.decce.transformingbase.constants.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.async.BasicAsyncLoggerContextSelector;
import org.apache.logging.log4j.core.impl.Log4jContextFactory;

import java.util.List;


public class LoggerConfigurator {
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

    static void configure() {
        var test = AsyncLogger.config.testPerformance;
        List<LoggerTester.Result> before = null;
        List<LoggerTester.Result> after = null;
        if (test) {
            before = LoggerTester.testAll();
        }

        configureDebugLog();

        var selector = new BasicAsyncLoggerContextSelector();
        LogManager.setFactory(new Log4jContextFactory(selector));
        if (AsyncLogger.config.filtering) {
            // https://logging.apache.org/log4j/2.x/manual/filters.html#filtering-process
            if (AsyncLogger.config.filterGlobal) {
                // Filter in the "Logger" stage (earliest stage)
                LoggerContext.getContext(false).addFilter(new AsyncFilter());
            }
            else {
                // Filter in the "LoggerConfig" stage (2nd stage, earliest after LogEvent creation)
                ((Logger) LogManager.getRootLogger()).addFilter(new AsyncFilter());
            }
        }
        if (AsyncLogger.config.wrapSysOutSysErr) {
            configureSysOutErr();
        }

        configureDebugLog();

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

    private static void configureDebugLog() {
        if (AsyncLogger.config.noDebugLog) {
            var configuration = LoggerContext.getContext(false).getConfiguration();
            configuration.getRootLogger().removeAppender("DebugFile");
        }
    }

    private static void configureSysOutErr() {
        System.setOut(new WrappedPrintStream("STDOUT", System.out));
        System.setErr(new WrappedPrintStream("STDERR", System.err));
    }
}
