package me.decce.transformingbase.core;

import com.github.bsideup.jabel.Desugar;
import me.decce.transformingbase.constants.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

public class LoggerTester {
    public static List<Result> testAll() {
        var logger = LogManager.getLogger("Test Logger");
        warmup(logger);
        logger.info("Starting Test");
        return Arrays.asList(testString(logger), testPlaceholder(logger), testThrowable(logger));
    }

    private static void warmup(Logger logger) {
        for (int i = 0; i < 50; i++) {
            logger.info("AsyncLogger Warmup Message");
            logger.info("AsyncLogger Warmup Message {}", "Placeholder");
            logger.info("AsyncLogger Warmup Message", new TestThrowable());
        }
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ignored) {
        }
    }

    private static Result testString(Logger logger) {
        Thread.yield();
        long nano1 = System.nanoTime();
        for (int i = 0; i < Item.STRING.count; i++) {
            logger.info("AsyncLogger Test Message");
            Thread.yield();
        }
        long nano2 = System.nanoTime();
        return new Result(Item.STRING, nano2 - nano1);
    }

    private static Result testPlaceholder(Logger logger) {
        Thread.yield();
        long nano1 = System.nanoTime();
        for (int i = 0; i < Item.PLACEHOLDER.count; i++) {
            logger.info("{} Message with Placeholder {}", Constants.MOD_NAME, i);
            Thread.yield();
        }
        long nano2 = System.nanoTime();
        return new Result(Item.PLACEHOLDER, nano2 - nano1);
    }

    private static Result testThrowable(Logger logger) {
        Thread.yield();
        long nano1 = System.nanoTime();
        var throwable = new TestThrowable();
        for (int i = 0; i < Item.THROWABLE.count; i++) {
            logger.info("Message with Throwable", throwable);
            Thread.yield();
        }
        long nano2 = System.nanoTime();
        return new Result(Item.THROWABLE, nano2 - nano1);
    }

    private enum Item {
        STRING("Simple Messages", 10000),
        PLACEHOLDER("Messages with Placeholders", 10000),
        THROWABLE("Messages with Throwable", 1500);

        final String displayName;
        final int count;

        Item(String displayName, int count) {
            this.displayName = displayName;
            this.count = count;
        }

        @Override
        public String toString() {
            return String.format("%s (%d in total)", displayName, count);
        }
    }

    @Desugar
    public record Result(Item item, long elapsedNanos) {
        public String elapsedTimeInMs() {
            return String.format("%.1f", elapsedNanos * 1d / 1_000_000d);
        }
    }

    private static class TestThrowable extends RuntimeException {
        public TestThrowable() {
            super("(this is not an error)");
        }
    }
}
