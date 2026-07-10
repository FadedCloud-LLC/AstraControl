package dev.aponder.astracontrol.plugins;

import java.util.List;

/**
 * Read-only snapshot of a single installed plugin's metadata, used by the plugin
 * list/detail GUI menus and {@code /astractrl plugin <name>}.
 */
public record PluginInfo(
        String name,
        String version,
        List<String> authors,
        boolean enabled,
        String mainClass,
        List<String> depends,
        List<String> softDepends,
        String website,
        String description
) {
}
