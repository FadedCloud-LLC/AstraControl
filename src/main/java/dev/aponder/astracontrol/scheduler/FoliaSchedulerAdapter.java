package dev.aponder.astracontrol.scheduler;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;

/**
 * {@link SchedulerAdapter} backed by Folia's regionized schedulers. Only constructed
 * when {@link dev.aponder.astracontrol.health.FoliaDetector} confirms the server is
 * running Folia. General-purpose work runs on the global region scheduler; work that
 * touches a specific entity or world location is routed to the owning region so we
 * never assume a single main thread.
 */
public final class FoliaSchedulerAdapter implements SchedulerAdapter {

    private static final long TICK_MILLIS = 50L;

    private final Plugin plugin;
    private final TaskRegistry<FoliaScheduledTask> activeTasks = new TaskRegistry<>();

    public FoliaSchedulerAdapter(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ScheduledTask runNow(Runnable task) {
        return track(plugin.getServer().getGlobalRegionScheduler().run(plugin, handle -> task.run()));
    }

    @Override
    public ScheduledTask runAsync(Runnable task) {
        return track(plugin.getServer().getAsyncScheduler().runNow(plugin, handle -> task.run()));
    }

    @Override
    public ScheduledTask runLater(Runnable task, long delayTicks) {
        return track(plugin.getServer().getGlobalRegionScheduler()
                .runDelayed(plugin, handle -> task.run(), Math.max(1L, delayTicks)));
    }

    @Override
    public ScheduledTask runLaterAsync(Runnable task, long delayTicks) {
        return track(plugin.getServer().getAsyncScheduler()
                .runDelayed(plugin, handle -> task.run(), Math.max(1L, delayTicks) * TICK_MILLIS, TimeUnit.MILLISECONDS));
    }

    @Override
    public ScheduledTask runTimer(Runnable task, long delayTicks, long periodTicks) {
        return track(plugin.getServer().getGlobalRegionScheduler()
                .runAtFixedRate(plugin, handle -> task.run(), Math.max(1L, delayTicks), Math.max(1L, periodTicks)));
    }

    @Override
    public ScheduledTask runTimerAsync(Runnable task, long delayTicks, long periodTicks) {
        return track(plugin.getServer().getAsyncScheduler().runAtFixedRate(
                plugin,
                handle -> task.run(),
                Math.max(1L, delayTicks) * TICK_MILLIS,
                Math.max(1L, periodTicks) * TICK_MILLIS,
                TimeUnit.MILLISECONDS));
    }

    @Override
    public ScheduledTask runAtEntity(Entity entity, Runnable task) {
        io.papermc.paper.threadedregions.scheduler.ScheduledTask scheduled =
                entity.getScheduler().run(plugin, handle -> task.run(), null);
        if (scheduled == null) {
            // Entity was already retired; nothing further to do.
            return NoOpTask.INSTANCE;
        }
        return track(scheduled);
    }

    @Override
    public ScheduledTask runAtEntityLater(Entity entity, Runnable task, long delayTicks) {
        io.papermc.paper.threadedregions.scheduler.ScheduledTask scheduled =
                entity.getScheduler().runDelayed(plugin, handle -> task.run(), null, Math.max(1L, delayTicks));
        if (scheduled == null) {
            return NoOpTask.INSTANCE;
        }
        return track(scheduled);
    }

    @Override
    public ScheduledTask runAtLocation(Location location, Runnable task) {
        return track(plugin.getServer().getRegionScheduler().run(plugin, location, handle -> task.run()));
    }

    @Override
    public void cancelAllTasks() {
        activeTasks.cancelAll(FoliaScheduledTask::cancel);
    }

    private ScheduledTask track(io.papermc.paper.threadedregions.scheduler.ScheduledTask foliaTask) {
        return activeTasks.track(new FoliaScheduledTask(foliaTask, activeTasks));
    }

    private static final class FoliaScheduledTask implements ScheduledTask {

        private final io.papermc.paper.threadedregions.scheduler.ScheduledTask task;
        private final TaskRegistry<FoliaScheduledTask> registry;

        private FoliaScheduledTask(io.papermc.paper.threadedregions.scheduler.ScheduledTask task,
                                    TaskRegistry<FoliaScheduledTask> registry) {
            this.task = task;
            this.registry = registry;
        }

        @Override
        public void cancel() {
            try {
                task.cancel();
            } catch (RuntimeException ignored) {
                // Already retired/cancelled by Folia; ensure the registry is still cleaned up.
            } finally {
                registry.remove(this);
            }
        }

        @Override
        public boolean isCancelled() {
            return task.isCancelled();
        }
    }

    private enum NoOpTask implements ScheduledTask {
        INSTANCE;

        @Override
        public void cancel() {
        }

        @Override
        public boolean isCancelled() {
            return true;
        }
    }
}
