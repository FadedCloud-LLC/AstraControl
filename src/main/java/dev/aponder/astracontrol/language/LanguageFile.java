package dev.aponder.astracontrol.language;

import dev.aponder.astracontrol.config.ConfigFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A single {@code language/<code>.yml} resource.
 */
public final class LanguageFile {

    private final String code;
    private final ConfigFile configFile;

    public LanguageFile(JavaPlugin plugin, String code) {
        this.code = code;
        this.configFile = new ConfigFile(plugin, "language/" + code + ".yml");
    }

    public String code() {
        return code;
    }

    public void reload() {
        configFile.reload();
    }

    public String get(String path) {
        return configFile.getConfiguration().getString(path);
    }

    public boolean has(String path) {
        return configFile.getConfiguration().isString(path);
    }
}
