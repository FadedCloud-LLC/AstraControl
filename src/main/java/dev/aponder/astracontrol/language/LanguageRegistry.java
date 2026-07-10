package dev.aponder.astracontrol.language;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds every {@link LanguageFile} that has been loaded, keyed by language code.
 * AstraControl ships only {@code en}, but server owners may drop additional
 * {@code language/<code>.yml} files in and switch to them via {@code config.yml}.
 */
public final class LanguageRegistry {

    private final JavaPlugin plugin;
    private final Map<String, LanguageFile> languages = new ConcurrentHashMap<>();

    public LanguageRegistry(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public LanguageFile get(String code) {
        return languages.computeIfAbsent(code, c -> new LanguageFile(plugin, c));
    }

    public void reloadAll() {
        languages.values().forEach(LanguageFile::reload);
    }
}
