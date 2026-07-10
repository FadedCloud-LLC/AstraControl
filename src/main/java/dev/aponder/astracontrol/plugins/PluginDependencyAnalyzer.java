package dev.aponder.astracontrol.plugins;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Cross-references a plugin's declared dependencies against what is actually
 * installed and enabled right now.
 */
public final class PluginDependencyAnalyzer {

    public record DependencyStatus(String name, boolean required, boolean present, boolean enabled) {
    }

    public List<DependencyStatus> analyze(PluginInfo info) {
        List<DependencyStatus> statuses = new ArrayList<>();
        for (String dependency : info.depends()) {
            statuses.add(statusOf(dependency, true));
        }
        for (String dependency : info.softDepends()) {
            statuses.add(statusOf(dependency, false));
        }
        return statuses;
    }

    private DependencyStatus statusOf(String name, boolean required) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
        return new DependencyStatus(name, required, plugin != null, plugin != null && plugin.isEnabled());
    }
}
