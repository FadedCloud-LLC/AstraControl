package dev.aponder.astracontrol.metrics;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Optional bStats integration. Collects no personal data - only the standard
 * anonymous bStats server metrics (player count, server software, versions).
 * bStats classes are shaded and relocated under
 * {@code dev.aponder.astracontrol.libs.bstats} at build time.
 */
public final class MetricsManager {

    private static final int PLUGIN_ID = 32484;

    private final JavaPlugin plugin;
    private Metrics metrics;

    public MetricsManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (metrics != null) {
            return;
        }
        metrics = new Metrics(plugin, PLUGIN_ID);
    }
}
