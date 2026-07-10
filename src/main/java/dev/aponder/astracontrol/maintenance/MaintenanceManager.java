package dev.aponder.astracontrol.maintenance;

import dev.aponder.astracontrol.config.ConfigManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Owns the runtime maintenance flag/reason. When {@code maintenance.persist} is true
 * in {@code config.yml}, state survives a restart via a small
 * {@code maintenance-state.yml} file in the plugin's data folder. Players already
 * online are never affected by a toggle - only new logins are gated, via
 * {@link MaintenanceListener}.
 */
public final class MaintenanceManager {

    private final JavaPlugin plugin;
    private final ConfigManager config;
    private final File stateFile;

    private volatile boolean enabled;
    private volatile String reason;

    public MaintenanceManager(JavaPlugin plugin, ConfigManager config) {
        this.plugin = plugin;
        this.config = config;
        this.stateFile = new File(plugin.getDataFolder(), "maintenance-state.yml");
        load();
    }

    private void load() {
        if (config.isMaintenancePersistent() && stateFile.exists()) {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(stateFile);
            enabled = yaml.getBoolean("enabled", config.isMaintenanceEnabledByDefault());
            reason = yaml.getString("reason", config.getMaintenanceReason());
        } else {
            enabled = config.isMaintenanceEnabledByDefault();
            reason = config.getMaintenanceReason();
        }
    }

    public MaintenanceState state() {
        return new MaintenanceState(enabled, reason);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String reason() {
        return reason;
    }

    public void enable(String customReason) {
        enabled = true;
        if (customReason != null && !customReason.isBlank()) {
            reason = customReason;
        }
        persist();
    }

    public void disable() {
        enabled = false;
        persist();
    }

    public void setReason(String newReason) {
        reason = newReason;
        persist();
    }

    private void persist() {
        if (!config.isMaintenancePersistent()) {
            return;
        }
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("enabled", enabled);
        yaml.set("reason", reason);
        try {
            yaml.save(stateFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to persist maintenance state", e);
        }
    }
}
