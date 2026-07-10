package dev.aponder.astracontrol.scheduler;

import dev.aponder.astracontrol.health.FoliaDetector;
import org.bukkit.plugin.Plugin;

/**
 * Selects the correct {@link SchedulerAdapter} implementation for the platform the
 * plugin is currently running on.
 */
public final class SchedulerUtil {

    private SchedulerUtil() {
    }

    public static SchedulerAdapter create(Plugin plugin) {
        if (FoliaDetector.isFolia()) {
            return new FoliaSchedulerAdapter(plugin);
        }
        return new BukkitSchedulerAdapter(plugin);
    }
}
