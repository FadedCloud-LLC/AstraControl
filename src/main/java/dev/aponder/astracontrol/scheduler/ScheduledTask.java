package dev.aponder.astracontrol.scheduler;

/**
 * A handle to a task scheduled through a {@link SchedulerAdapter}.
 * Abstracts over Bukkit's {@code BukkitTask} and Folia's region/global/async task handles.
 */
public interface ScheduledTask {

    void cancel();

    boolean isCancelled();
}
