package me.decce.transformingbase.service;

import me.decce.transformingbase.constants.Constants;
import org.apache.logging.log4j.LogManager;
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

        var selector = new BasicAsyncLoggerContextSelector();
        LogManager.setFactory(new Log4jContextFactory(selector));
        var logger = LogManager.getLogger(Constants.MOD_NAME);
        if (AsyncLogger.config.wrapSysOutSysErr) {
            configureSysOutErr();
            logger.info("Successfully configured async logger context and wrapped System.out and System.err");
        }
        else {
            logger.info("Successfully configured async logger context");
        }

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

    private static void configureSysOutErr() {
        System.setOut(new WrappedPrintStream("STDOUT", System.out));
        System.setErr(new WrappedPrintStream("STDERR", System.err));
    }
}
