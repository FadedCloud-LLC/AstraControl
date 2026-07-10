package dev.aponder.astracontrol.scheduler;

import org.bukkit.entity.Entity;
import org.bukkit.Location;

/**
 * Abstracts task scheduling so the plugin behaves correctly on both the classic
 * single-threaded Bukkit/Spigot/Paper scheduler and Folia's regionized schedulers.
 * No code outside this package should touch {@code Bukkit.getScheduler()} or the
 * Folia region/global/async schedulers directly.
 */
public interface SchedulerAdapter {

    ScheduledTask runNow(Runnable task);

    ScheduledTask runAsync(Runnable task);

    ScheduledTask runLater(Runnable task, long delayTicks);

    ScheduledTask runLaterAsync(Runnable task, long delayTicks);

    ScheduledTask runTimer(Runnable task, long delayTicks, long periodTicks);

    ScheduledTask runTimerAsync(Runnable task, long delayTicks, long periodTicks);

    /**
     * Runs the task on the region/thread that owns the given entity, or the main
     * thread on non-Folia platforms.
     */
    ScheduledTask runAtEntity(Entity entity, Runnable task);

    /**
     * Like {@link #runAtEntity(Entity, Runnable)}, but delayed by the given number of
     * ticks.
     */
    ScheduledTask runAtEntityLater(Entity entity, Runnable task, long delayTicks);

    /**
     * Runs the task on the region/thread that owns the given location, or the main
     * thread on non-Folia platforms.
     */
    ScheduledTask runAtLocation(Location location, Runnable task);

    void cancelAllTasks();
}
