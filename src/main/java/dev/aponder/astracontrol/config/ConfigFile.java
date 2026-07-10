package dev.aponder.astracontrol.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

/**
 * A single YAML resource that ships inside the plugin jar, is copied to the data
 * folder on first load, and has its missing keys backfilled from the jar defaults on
 * every subsequent load without overwriting values the server owner has changed.
 */
public final class ConfigFile {

    private final JavaPlugin plugin;
    private final String resourcePath;
    private File file;
    private YamlConfiguration configuration;

    public ConfigFile(JavaPlugin plugin, String resourcePath) {
        this.plugin = plugin;
        this.resourcePath = resourcePath;
        load();
    }

    public void load() {
        file = new File(plugin.getDataFolder(), resourcePath);
        if (!file.exists()) {
            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }
            plugin.saveResource(resourcePath, false);
        }

        configuration = YamlConfiguration.loadConfiguration(file);

        try (InputStream defaultStream = plugin.getResource(resourcePath)) {
            if (defaultStream != null) {
                YamlConfiguration defaults = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
                configuration.setDefaults(defaults);
                configuration.options().copyDefaults(true);
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to read bundled defaults for " + resourcePath, e);
        }
    }

    public void reload() {
        load();
    }

    public void save() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save " + resourcePath, e);
        }
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    public String getResourcePath() {
        return resourcePath;
    }
}
