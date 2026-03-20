package me.decce.transformingbase.core;

import java.lang.invoke.MethodHandle;

public class LibraryAccessor {
    public static Object logger;
    public static MethodHandle logInfoString;
    public static MethodHandle logInfoObject;
    public static MethodHandle logInfoStringObject;
    public static MethodHandle logWarnString;
    public static MethodHandle logWarnObject;
    public static MethodHandle logWarnStringObject;
    public static MethodHandle logErrorString;
    public static MethodHandle logErrorObject;
    public static MethodHandle logErrorStringObject;
    public static MethodHandle logFatalString;
    public static MethodHandle logFatalObject;
    public static MethodHandle logFatalStringObject;
    public static MethodHandle logDebugString;
    public static MethodHandle logDebugObject;
    public static MethodHandle logDebugStringObject;

    public static void debug(String s) {
        try {
            logDebugString.invoke(logger, s);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public static void error(String s) {
        try {
            logErrorString.invoke(logger, s);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public static void fatal(String s, Object o) {
        try {
            logFatalStringObject.invoke(logger, s, o);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public static void fatal(Object o) {
        try {
            logFatalObject.invoke(logger, o);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public static void error(Object o) {
        try {
            logErrorObject.invoke(logger, o);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public static void warn(Object o) {
        try {
            logWarnObject.invoke(logger, o);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public static void info(String s) {
        try {
            logInfoString.invoke(logger, s);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public static void info(Object o) {
        try {
            logInfoObject.invoke(logger, o);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public static void info(String s, Object o) {
        try {
            logInfoStringObject.invoke(logger, s, o);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public static void debug(Object o) {
        try {
            logDebugObject.invoke(logger, o);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public static void error(String s, Object o) {
        try {
            logErrorStringObject.invoke(logger, s, o);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
