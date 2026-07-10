package dev.aponder.astracontrol.scheduler;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

/**
 * {@link SchedulerAdapter} backed by the classic single-threaded Bukkit scheduler.
 * Used on Spigot, Paper, and Purpur when Folia is not detected.
 */
public final class BukkitSchedulerAdapter implements SchedulerAdapter {

    private final Plugin plugin;
    private final BukkitScheduler scheduler;
    private final TaskRegistry<BukkitScheduledTask> activeTasks = new TaskRegistry<>();

    public BukkitSchedulerAdapter(Plugin plugin) {
        this.plugin = plugin;
        this.scheduler = plugin.getServer().getScheduler();
    }

    @Override
    public ScheduledTask runNow(Runnable task) {
        return track(scheduler.runTask(plugin, task));
    }

    @Override
    public ScheduledTask runAsync(Runnable task) {
        return track(scheduler.runTaskAsynchronously(plugin, task));
    }

    @Override
    public ScheduledTask runLater(Runnable task, long delayTicks) {
        return track(scheduler.runTaskLater(plugin, task, delayTicks));
    }

    @Override
    public ScheduledTask runLaterAsync(Runnable task, long delayTicks) {
        return track(scheduler.runTaskLaterAsynchronously(plugin, task, delayTicks));
    }

    @Override
    public ScheduledTask runTimer(Runnable task, long delayTicks, long periodTicks) {
        return track(scheduler.runTaskTimer(plugin, task, delayTicks, periodTicks));
    }

    @Override
    public ScheduledTask runTimerAsync(Runnable task, long delayTicks, long periodTicks) {
        return track(scheduler.runTaskTimerAsynchronously(plugin, task, delayTicks, periodTicks));
    }

    @Override
    public ScheduledTask runAtEntity(Entity entity, Runnable task) {
        return runNow(task);
    }

    @Override
    public ScheduledTask runAtEntityLater(Entity entity, Runnable task, long delayTicks) {
        return runLater(task, delayTicks);
    }

    @Override
    public ScheduledTask runAtLocation(Location location, Runnable task) {
        return runNow(task);
    }

    @Override
    public void cancelAllTasks() {
        activeTasks.cancelAll(BukkitScheduledTask::cancel);
    }

    private ScheduledTask track(BukkitTask bukkitTask) {
        return activeTasks.track(new BukkitScheduledTask(bukkitTask, activeTasks));
    }

    private static final class BukkitScheduledTask implements ScheduledTask {

        private final BukkitTask task;
        private final TaskRegistry<BukkitScheduledTask> registry;
        private volatile boolean cancelled;

        private BukkitScheduledTask(BukkitTask task, TaskRegistry<BukkitScheduledTask> registry) {
            this.task = task;
            this.registry = registry;
        }

        @Override
        public void cancel() {
            if (cancelled) {
                return;
            }
            cancelled = true;
            try {
                task.cancel();
            } catch (IllegalStateException ignored) {
                // Scheduler already shut down.
            }
            registry.remove(this);
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }
    }
}
