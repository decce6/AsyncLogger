package me.decce.transformingbase.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "unused"})
public class AsyncLoggerConfig {
    @Comment("Specifies whether to enable the mod")
    public boolean enabled = true;
    @Comment("Specifies the value for log4j2.asyncLoggerRingBufferSize (0 = use mod default, -1 = use log4j2 default)")
    public int ringBufferSize = 0;
    @Comment("Specifies the value for log4j2.asyncLoggerWaitStrategy (leave empty for default)")
    public String waitStrategy = "";
    @Comment("Specifies the value for log4j2.asyncLoggerSynchronizeEnqueueWhenQueueFull (leave empty for default)")
    public String synchronizeEnqueueWhenQueueFull = "";
    @Comment("Specifies the value for log4j2.formatMsgAsync (leave empty for default)")
    public String formatMsgAsync = "";
    @Comment("Specifies the value for log4j2.asyncQueueFullPolicy (leave empty for default)")
    public String asyncQueueFullPolicy = "";
    @Comment("Specifies the value for log4j2.discardThreshold (leave empty for default)")
    public String discardThreshold = "";
    @Comment("Specifies whether System.out and System.err should be redirected to the logger")
    public boolean wrapSysOutSysErr = false;
    @Comment("Forge/NeoForge-only: disables writing to `debug.log`")
    public boolean noDebugLog = false;
    @Comment("When enabled, compares logging performance without and with AsyncLogger, and logs the test results. Will cause a mass amount of messages to be printed at startup.")
    public boolean testPerformance = false;
    @Comment("Specifies whether to enable filtering for logs.\n" +
            "Unless \"global\" is set to true, filtering happens on the dedicated async thread, with no overhead for the caller.")
    @Key("filtering.enabled")
    public boolean filtering;
    @Comment("When set to true, filtering would happen globally instead of at the logger stage.\n" +
            "This can avoid creating the LogEvent, at the cost of performing filtering on the caller thread, and not being able to process placeholders properly.")
    @Key("filtering.global")
    public boolean filterGlobal;
    @Comment("The specified logging levels will be filtered out and not logged. Valid values: TRACE, DEBUG, INFO, WARN, ERROR, FATAL")
    @Key("filtering.levels")
    public List<String> levels = List.of();
    @Comment("Messages logged by loggers with the specified named will be filtered out and not logged.")
    @Key("filtering.loggers")
    public List<String> loggers = List.of();
    @Comment("Messages containing the specified substrings will be filtered out and not logged.")
    @Key("filtering.strings")
    public List<String> strings = List.of();
    @Comment("Messages that match the specified regexes will be filtered out and not logged.")
    @Key("filtering.regexes")
    public List<String> regexes = List.of();

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Comment {
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Key {
        String value();
    }
}
