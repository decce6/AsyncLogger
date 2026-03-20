package me.decce.transformingbase.service;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import me.decce.transformingbase.constants.Constants;
import me.decce.transformingbase.core.ExampleModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigLoader {
    private static final Logger LOGGER = LogManager.getLogger(Constants.MOD_NAME);
    private static final Path CONFIG_PATH;
    private static final Path CONFIG_FILE;

    static {
        CONFIG_PATH = Paths.get("config");
        CONFIG_FILE = CONFIG_PATH.resolve(Constants.MOD_ID + ".toml");
        try {
            if (!Files.exists(CONFIG_PATH)) {
                Files.createDirectories(CONFIG_PATH);
            }
        } catch (IOException ignored) {}
    }

    private static CommentedFileConfig makeNightConfig() {
        return CommentedFileConfig.builder(CONFIG_FILE, TomlFormat.instance())
                .preserveInsertionOrder()
                .sync()
                .build();
    }

    public static void save(ExampleModConfig config) {
        try (var night = toNightConfig(config)) {
            night.save();
        } catch (Exception e) {
            LOGGER.error("Failed to save configuration!", e);
        }
    }

    public static ExampleModConfig load() {
        return loadConfig();
    }

    private static ExampleModConfig loadConfig() {
        if (CONFIG_FILE.toFile().exists()) {
            try {
                return fromNightConfig();
            } catch (Exception e) {
                LOGGER.error("Failed to read configuration!", e);
            }
        }
        return new ExampleModConfig();
    }

    private static CommentedFileConfig toNightConfig(ExampleModConfig config) {
        var night = makeNightConfig();
        try {
            for (Field field : ExampleModConfig.class.getDeclaredFields()) {
                var modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || Modifier.isFinal(modifiers)) {
                    continue;
                }
                night.set(field.getName(), field.get(config));
                if (field.isAnnotationPresent(ExampleModConfig.Comment.class)) {
                    night.setComment(field.getName(), field.getAnnotation(ExampleModConfig.Comment.class).value());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return night;
    }

    private static ExampleModConfig fromNightConfig() {
        var config = new ExampleModConfig();
        try (var night = makeNightConfig()) {
            night.load();
            for (Field field : ExampleModConfig.class.getDeclaredFields()) {
                var modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || Modifier.isFinal(modifiers)) {
                    continue;
                }
                if (night.contains(field.getName())) {
                    field.set(config, night.get(field.getName()));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return config;
    }
}
