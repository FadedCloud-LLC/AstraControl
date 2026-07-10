package dev.aponder.astracontrol.lifecycle;

import dev.aponder.astracontrol.logging.PluginLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Runs every registered reload step (config, language, hooks, ...) in order and
 * reports whether the reload as a whole succeeded. Steps are registered once, at
 * startup, by whichever manager owns that piece of state - this class never
 * constructs or knows about any of them directly, so reloading never re-registers
 * listeners, tasks, placeholders, hooks, or menus twice.
 */
public final class ReloadManager {

    private final PluginLogger logger;
    private final List<Runnable> steps = new ArrayList<>();

    public ReloadManager(PluginLogger logger) {
        this.logger = logger;
    }

    public void addStep(Runnable step) {
        steps.add(step);
    }

    public ReloadResult reload() {
        long start = System.currentTimeMillis();
        try {
            for (Runnable step : steps) {
                step.run();
            }
            return ReloadResult.success(System.currentTimeMillis() - start);
        } catch (Exception e) {
            logger.error("AstraControl reload failed", e);
            return ReloadResult.failure(e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
        }
    }
}
