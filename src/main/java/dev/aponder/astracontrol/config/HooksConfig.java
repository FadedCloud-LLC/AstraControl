package dev.aponder.astracontrol.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Wraps {@code hooks.yml}, exposing per-integration configuration such as display
 * name overrides, whether a hook should be checked at all, and an optional configured
 * reload command.
 */
public final class HooksConfig {

    private final ConfigFile file;

    public HooksConfig(JavaPlugin plugin) {
        this.file = new ConfigFile(plugin, "hooks.yml");
    }

    public void reload() {
        file.reload();
    }

    public boolean isEnabled(String hookId) {
        return file.getConfiguration().getBoolean("hooks." + hookId + ".enabled", true);
    }

    public String displayName(String hookId, String fallback) {
        return file.getConfiguration().getString("hooks." + hookId + ".display-name", fallback);
    }

    public String pluginName(String hookId, String fallback) {
        return file.getConfiguration().getString("hooks." + hookId + ".plugin-name", fallback);
    }

    public String notes(String hookId, String fallback) {
        return file.getConfiguration().getString("hooks." + hookId + ".notes", fallback);
    }

    public String reloadCommand(String hookId) {
        return file.getConfiguration().getString("hooks." + hookId + ".reload-command", null);
    }

    public boolean showMissingInConsole() {
        return file.getConfiguration().getBoolean("settings.log-missing-hooks", false);
    }

    /**
     * Every hook id configured under {@code hooks:} that isn't in {@code knownIds}
     * (the ids already backed by a dedicated {@link dev.aponder.astracontrol.hooks.Hook}
     * class). Lets {@code HookManager} watch any plugin a server owner adds to
     * {@code hooks.yml} generically, without an AstraControl code change.
     */
    public Set<String> additionalHookIds(Set<String> knownIds) {
        ConfigurationSection section = file.getConfiguration().getConfigurationSection("hooks");
        if (section == null) {
            return Set.of();
        }
        Set<String> ids = new LinkedHashSet<>(section.getKeys(false));
        ids.removeAll(knownIds);
        return ids;
    }
}
