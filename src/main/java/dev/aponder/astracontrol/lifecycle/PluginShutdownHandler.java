package dev.aponder.astracontrol.lifecycle;

import dev.aponder.astracontrol.errors.ErrorWatcher;
import dev.aponder.astracontrol.gui.MenuSessionManager;
import dev.aponder.astracontrol.health.ServerHealthManager;
import dev.aponder.astracontrol.scheduler.SchedulerAdapter;

/**
 * Runs on plugin disable: stops the health monitor, detaches the log appender,
 * clears every GUI session, and cancels every outstanding scheduled task. Bukkit
 * already unregisters this plugin's listeners/commands automatically, so this class
 * only needs to clean up AstraControl's own runtime state.
 */
public final class PluginShutdownHandler {

    private final SchedulerAdapter scheduler;
    private final MenuSessionManager sessionManager;
    private final ErrorWatcher errorWatcher;
    private final ServerHealthManager healthManager;

    public PluginShutdownHandler(SchedulerAdapter scheduler,
                                  MenuSessionManager sessionManager,
                                  ErrorWatcher errorWatcher,
                                  ServerHealthManager healthManager) {
        this.scheduler = scheduler;
        this.sessionManager = sessionManager;
        this.errorWatcher = errorWatcher;
        this.healthManager = healthManager;
    }

    public void shutdown() {
        healthManager.stop();
        errorWatcher.detachFromRootLogger();
        sessionManager.clearAll();
        scheduler.cancelAllTasks();
    }
}
