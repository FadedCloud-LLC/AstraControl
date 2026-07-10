package dev.aponder.astracontrol.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.function.Consumer;

/**
 * Central access point for {@code config.yml}, plus the {@link GuiConfig} and
 * {@link HooksConfig} companion files. All typed getters live here so the rest of the
 * codebase never touches raw YAML paths.
 */
public final class ConfigManager {

    private final ConfigFile configFile;
    private final GuiConfig guiConfig;
    private final HooksConfig hooksConfig;
    private final ConfigValidator validator = new ConfigValidator();

    public ConfigManager(JavaPlugin plugin) {
        this.configFile = new ConfigFile(plugin, "config.yml");
        this.guiConfig = new GuiConfig(plugin);
        this.hooksConfig = new HooksConfig(plugin);
    }

    public void reload(Consumer<String> validationWarningSink) {
        configFile.reload();
        guiConfig.reload();
        hooksConfig.reload();
        validator.validateAndReport(cfg(), validationWarningSink);
    }

    public List<String> validateNow() {
        return validator.validate(cfg());
    }

    private FileConfiguration cfg() {
        return configFile.getConfiguration();
    }

    public GuiConfig gui() {
        return guiConfig;
    }

    public HooksConfig hooks() {
        return hooksConfig;
    }

    public FileConfiguration raw() {
        return cfg();
    }

    // --- general ---

    public boolean isDebug() {
        return cfg().getBoolean("debug", false);
    }

    public String getLanguage() {
        return cfg().getString("language", "en");
    }

    public List<String> getAliases() {
        return cfg().getStringList("aliases");
    }

    // --- maintenance ---

    public boolean isMaintenancePersistent() {
        return cfg().getBoolean("maintenance.persist", true);
    }

    public boolean isMaintenanceEnabledByDefault() {
        return cfg().getBoolean("maintenance.enabled", false);
    }

    public String getMaintenanceReason() {
        return cfg().getString("maintenance.reason", "The server is currently undergoing maintenance.");
    }

    public String getMaintenanceBypassPermission() {
        return cfg().getString("maintenance.bypass-permission", "astracontrol.bypass.maintenance");
    }

    public boolean isMaintenanceBroadcastOnToggle() {
        return cfg().getBoolean("maintenance.broadcast-on-toggle", true);
    }

    // --- health ---

    public long getHealthUpdateIntervalTicks() {
        return cfg().getLong("health.update-interval-ticks", 100L);
    }

    public boolean isHealthWarningsEnabled() {
        return cfg().getBoolean("health.warnings.enabled", true);
    }

    public String getHealthNotifyPermission() {
        return cfg().getString("health.warnings.notify-permission", "astracontrol.notify.health");
    }

    public double getTpsWarningThreshold() {
        return cfg().getDouble("health.warnings.tps-threshold", 18.0);
    }

    public double getMsptWarningThreshold() {
        return cfg().getDouble("health.warnings.mspt-threshold", 50.0);
    }

    public double getMemoryWarningThresholdPercent() {
        return cfg().getDouble("health.warnings.memory-usage-threshold-percent", 90.0);
    }

    // --- error watcher ---

    public boolean isErrorWatcherEnabled() {
        return cfg().getBoolean("error-watcher.enabled", true);
    }

    public int getErrorBufferSize() {
        return cfg().getInt("error-watcher.buffer-size", 200);
    }

    public boolean isCapturePluginLogs() {
        return cfg().getBoolean("error-watcher.capture-plugin-logs", true);
    }

    public boolean isErrorNotifyInGame() {
        return cfg().getBoolean("error-watcher.notify-in-game", true);
    }

    public String getErrorNotifyPermission() {
        return cfg().getString("error-watcher.notify-permission", "astracontrol.notify.errors");
    }

    // --- broadcast ---

    public String getBroadcastChatFormat() {
        return cfg().getString("broadcast.chat-format", "<gray>[<gold>AstraControl<gray>] <white>{message}");
    }

    public int getBroadcastTitleFadeIn() {
        return cfg().getInt("broadcast.title.fade-in", 10);
    }

    public int getBroadcastTitleStay() {
        return cfg().getInt("broadcast.title.stay", 60);
    }

    public int getBroadcastTitleFadeOut() {
        return cfg().getInt("broadcast.title.fade-out", 10);
    }

    public int getBroadcastActionbarDurationTicks() {
        return cfg().getInt("broadcast.actionbar.duration-ticks", 60);
    }

    public boolean isBroadcastSoundEnabled() {
        return cfg().getBoolean("broadcast.sound.enabled", true);
    }

    public String getBroadcastSound() {
        return cfg().getString("broadcast.sound.sound", "entity.experience_orb.pickup");
    }

    public float getBroadcastSoundVolume() {
        return (float) cfg().getDouble("broadcast.sound.volume", 1.0);
    }

    public float getBroadcastSoundPitch() {
        return (float) cfg().getDouble("broadcast.sound.pitch", 1.0);
    }

    public boolean isBossbarEnabled() {
        return cfg().getBoolean("broadcast.bossbar.enabled", true);
    }

    public int getBossbarDurationSeconds() {
        return cfg().getInt("broadcast.bossbar.duration-seconds", 10);
    }

    public String getBossbarColor() {
        return cfg().getString("broadcast.bossbar.color", "YELLOW");
    }

    public String getBossbarOverlay() {
        return cfg().getString("broadcast.bossbar.overlay", "PROGRESS");
    }

    // --- placeholderapi ---

    public boolean isPapiEnabled() {
        return cfg().getBoolean("placeholderapi.enabled", true);
    }

    public boolean isPapiCacheResults() {
        return cfg().getBoolean("placeholderapi.cache-results", false);
    }

    // --- permission debugger ---

    public boolean isPreferLuckPerms() {
        return cfg().getBoolean("permission-debugger.prefer-luckperms", true);
    }

    public boolean isShowInheritedPermissions() {
        return cfg().getBoolean("permission-debugger.show-inherited-permissions", true);
    }

    // --- player tools ---

    public boolean isInventoryViewAllowed() {
        return cfg().getBoolean("player-tools.allow-inventory-view", true);
    }

    public boolean isTeleportAllowed() {
        return cfg().getBoolean("player-tools.allow-teleport", true);
    }

    public String getStaffCommandTemplate() {
        return cfg().getString("player-tools.staff-command", "");
    }

    // --- metrics ---

    public boolean isMetricsEnabled() {
        return cfg().getBoolean("metrics.enabled", true);
    }

    // --- update checker ---

    public boolean isUpdateCheckerEnabled() {
        return cfg().getBoolean("update-checker.enabled", false);
    }

    public boolean isUpdateCheckerNotifyAdmins() {
        return cfg().getBoolean("update-checker.notify-admins-on-join", false);
    }
}
