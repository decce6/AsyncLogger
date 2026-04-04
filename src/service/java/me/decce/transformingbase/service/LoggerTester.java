package me.decce.transformingbase.service;

import me.decce.transformingbase.constants.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class LoggerTester {
    public static List<Result> testAll() {
        var logger = LogManager.getLogger("Test Logger");
        logger.info("Starting Test");
        return List.of(testString(logger), testPlaceholder(logger), testThrowable(logger));
    }

    private static Result testString(Logger logger) {
        Thread.yield();
        long nano1 = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            logger.info("AsyncLogger Test Message");
        }
        long nano2 = System.nanoTime();
        return new Result(Item.STRING, nano2 - nano1);
    }

    private static Result testPlaceholder(Logger logger) {
        Thread.yield();
        long nano1 = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            logger.info("{} Message with Placeholder {}", Constants.MOD_NAME, i);
        }
        long nano2 = System.nanoTime();
        return new Result(Item.PLACEHOLDER, nano2 - nano1);
    }

    private static Result testThrowable(Logger logger) {
        Thread.yield();
        long nano1 = System.nanoTime();
        var throwable = new TestThrowable();
        for (int i = 0; i < 1500; i++) {
            logger.info("Message with Throwable", throwable);
        }
        long nano2 = System.nanoTime();
        return new Result(Item.THROWABLE, nano2 - nano1);
    }

    private enum Item {
        STRING("Simple Messages"),
        PLACEHOLDER("Messages with Placeholders"),
        THROWABLE("Messages with Throwable");

        final String displayName;

        Item(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

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
