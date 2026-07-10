package dev.aponder.astracontrol.gui;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Owns every player's {@link MenuSession}. Cleared on plugin reload/shutdown and on
 * player quit so no stale state survives across sessions.
 */
public final class MenuSessionManager {

    private final Map<UUID, MenuSession> sessions = new ConcurrentHashMap<>();

    public MenuSession getOrCreate(UUID playerId) {
        return sessions.computeIfAbsent(playerId, MenuSession::new);
    }

    public MenuSession get(UUID playerId) {
        return sessions.get(playerId);
    }

    public void remove(UUID playerId) {
        sessions.remove(playerId);
    }

    public void clearAll() {
        sessions.clear();
    }
}
