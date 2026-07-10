package dev.aponder.astracontrol.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Wraps {@code gui.yml}. Exposes raw configuration sections per menu; the
 * {@code dev.aponder.astracontrol.gui} package is responsible for parsing those
 * sections into {@code GuiLayout}/{@code GuiItem} instances.
 */
public final class GuiConfig {

    private final ConfigFile file;

    public GuiConfig(JavaPlugin plugin) {
        this.file = new ConfigFile(plugin, "gui.yml");
    }

    public void reload() {
        file.reload();
    }

    public ConfigurationSection menuSection(String menuKey) {
        return file.getConfiguration().getConfigurationSection("menus." + menuKey);
    }
}
