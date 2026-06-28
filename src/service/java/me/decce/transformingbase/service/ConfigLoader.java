package me.decce.transformingbase.service;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import me.decce.transformingbase.constants.Constants;
import me.decce.transformingbase.core.AsyncLoggerConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class ConfigLoader {
    private static final String DEFAULT_EXTRA_CONFIG =
            """
            # This is the default extra filter file for Async Logger. The filters in this file (and any other *.toml files in this directory) are combined with the filters in the main config.
            # You can freely write comments and format this file as you wish - it will not get overwritten.
            [filtering]
            levels = []
            loggers = []
            strings = []
            regexes = []
            """;
    private static final Logger LOGGER = LogManager.getLogger(Constants.MOD_NAME);
    private static final Path CONFIG_PATH;
    private static final Path CONFIG_EXTRA_PATH;
    private static final Path CONFIG_FILE;

    static {
        CONFIG_PATH = Paths.get("config");
        CONFIG_EXTRA_PATH = Paths.get("config", "asynclogger");
        CONFIG_FILE = CONFIG_PATH.resolve(Constants.MOD_ID + ".toml");
        try {
            if (!Files.exists(CONFIG_PATH)) {
                Files.createDirectories(CONFIG_PATH);
            }
            if (!Files.exists(CONFIG_EXTRA_PATH)) {
                Files.createDirectories(CONFIG_EXTRA_PATH);
                Files.writeString(CONFIG_EXTRA_PATH.resolve("default.toml"), DEFAULT_EXTRA_CONFIG, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            }
        } catch (IOException ignored) {}
    }

    private static CommentedFileConfig makeNightConfig(Path file) {
        return CommentedFileConfig.builder(file, TomlFormat.instance())
                .preserveInsertionOrder()
                .sync()
                .build();
    }

    public static void save(AsyncLoggerConfig config) {
        try (var night = toNightConfig(config)) {
            night.save();
        } catch (Exception e) {
            LOGGER.error("Failed to save configuration!", e);
        }
    }

    public static AsyncLoggerConfig load() {
        return loadConfig();
    }

    private static AsyncLoggerConfig loadConfig() {
        if (CONFIG_FILE.toFile().exists()) {
            try {
                return fromNightConfig(CONFIG_FILE);
            } catch (Exception e) {
                LOGGER.error("Failed to read configuration!", e);
            }
        }
        return new AsyncLoggerConfig();
    }

    public static void loadExtras(AsyncLoggerConfig config) {
        if (CONFIG_EXTRA_PATH.toFile().exists()) {
            try {
                try (var stream = Files.walk(CONFIG_EXTRA_PATH)) {
                    stream.filter(Files::isRegularFile).filter(file -> file.toString().endsWith(".toml")).forEach(file -> {
                        LOGGER.debug("Loaded extra filter {}", file.toString());
                        readExtras(file, config);
                    });
                }
            } catch (IOException e) {
                LOGGER.error("Failed to load extra filters!", e);
            }
        }
    }

    private static void readExtras(Path file, AsyncLoggerConfig config) {
        AsyncLoggerConfig extra = fromNightConfig(file);
        combineLists(config.levels, extra.levels);
        combineLists(config.loggers, extra.loggers);
        combineLists(config.strings, extra.strings);
        combineLists(config.regexes, extra.regexes);
    }

    private static <T> void combineLists(List<T> dest, List<T> source) {
        for (T t : source) {
            if (!dest.contains(t)) {
                dest.add(t);
            }
        }
    }

    private static CommentedFileConfig toNightConfig(AsyncLoggerConfig config) {
        var night = makeNightConfig(CONFIG_FILE);
        try {
            for (Field field : AsyncLoggerConfig.class.getDeclaredFields()) {
                var modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || Modifier.isFinal(modifiers)) {
                    continue;
                }
                String key = field.getName();
                if (field.isAnnotationPresent(AsyncLoggerConfig.Key.class)) {
                    key = field.getAnnotation(AsyncLoggerConfig.Key.class).value();
                }
                night.set(key, field.get(config));
                if (field.isAnnotationPresent(AsyncLoggerConfig.Comment.class)) {
                    night.setComment(key, field.getAnnotation(AsyncLoggerConfig.Comment.class).value());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return night;
    }

    private static AsyncLoggerConfig fromNightConfig(Path file) {
        var config = new AsyncLoggerConfig();
        try (var night = makeNightConfig(file)) {
            night.load();
            for (Field field : AsyncLoggerConfig.class.getDeclaredFields()) {
                var modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || Modifier.isFinal(modifiers)) {
                    continue;
                }
                String key = field.getName();
                if (field.isAnnotationPresent(AsyncLoggerConfig.Key.class)) {
                    key = field.getAnnotation(AsyncLoggerConfig.Key.class).value();
                }
                if (night.contains(key)) {
                    field.set(config, night.get(key));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return config;
    }
}
