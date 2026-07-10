package dev.aponder.astracontrol.plugins;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores server-owner-configured reload commands for specific plugins (sourced from
 * {@code hooks.yml}'s {@code reload-command} entries). AstraControl only ever runs a
 * command the owner explicitly configured - it never invents or guesses a reload
 * command, and it never unloads/reloads a plugin directly through the API.
 */
public final class PluginReloadRegistry {

    private final Map<String, String> commandsByPluginName = new ConcurrentHashMap<>();

    public void register(String pluginName, String command) {
        if (command != null && !command.isBlank()) {
            commandsByPluginName.put(pluginName, command);
        }
    }

    public Optional<String> commandFor(String pluginName) {
        return Optional.ofNullable(commandsByPluginName.get(pluginName));
    }

    public void clear() {
        commandsByPluginName.clear();
    }
}
