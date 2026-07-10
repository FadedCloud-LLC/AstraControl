package dev.aponder.astracontrol.logging;

import dev.aponder.astracontrol.hooks.HookStatus;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central, thread-safe store of the most recently detected {@link HookStatus} for
 * every known integration. Populated by {@code HookManager} and read by the startup
 * summary, the hooks command, and the hook status GUI menu.
 */
public final class HookStatusRegistry {

    private final Map<String, HookStatus> statuses = new ConcurrentHashMap<>();

    public void put(HookStatus status) {
        statuses.put(status.id(), status);
    }

    public HookStatus get(String id) {
        return statuses.get(id);
    }

    public Collection<HookStatus> all() {
        return statuses.values();
    }

    public Collection<HookStatus> activeHooks() {
        return statuses.values().stream().filter(HookStatus::enabled).toList();
    }

    public void clear() {
        statuses.clear();
    }
}
