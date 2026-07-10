package dev.aponder.astracontrol.scheduler;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Tracks the set of currently-active tasks for a {@link SchedulerAdapter} so they can
 * all be cancelled on shutdown/reload. Shared by {@link BukkitSchedulerAdapter} and
 * {@link FoliaSchedulerAdapter}, which otherwise duplicated this bookkeeping - the
 * platform-specific part is only how an individual task is cancelled.
 */
final class TaskRegistry<T> {

    private final Set<T> activeTasks = ConcurrentHashMap.newKeySet();

    T track(T task) {
        activeTasks.add(task);
        return task;
    }

    void remove(T task) {
        activeTasks.remove(task);
    }

    void cancelAll(Consumer<T> cancel) {
        for (T task : activeTasks) {
            cancel.accept(task);
        }
        activeTasks.clear();
    }
}
