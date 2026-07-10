package dev.aponder.astracontrol.plugins;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Reads installed plugin metadata via the standard {@code PluginManager} API. Purely
 * informational - never loads, unloads, enables, or disables anything.
 */
public final class PluginInspector {

    public List<PluginInfo> allPlugins() {
        return Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .map(this::toInfo)
                .sorted((a, b) -> a.name().compareToIgnoreCase(b.name()))
                .toList();
    }

    public Optional<PluginInfo> find(String name) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
        return plugin == null ? Optional.empty() : Optional.of(toInfo(plugin));
    }

    private PluginInfo toInfo(Plugin plugin) {
        PluginDescriptionFile desc = plugin.getDescription();
        return new PluginInfo(
                desc.getName(),
                desc.getVersion(),
                desc.getAuthors(),
                plugin.isEnabled(),
                desc.getMain(),
                desc.getDepend(),
                desc.getSoftDepend(),
                desc.getWebsite(),
                desc.getDescription()
        );
    }
}
