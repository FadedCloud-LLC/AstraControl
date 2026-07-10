package dev.aponder.astracontrol.commands;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks per-player, per-command last-use timestamps so spam-prone commands
 * (reload, broadcast) can enforce a short cooldown. Never applied to console.
 */
public final class CommandCooldownManager {

    private final Map<UUID, Map<String, Long>> lastUse = new ConcurrentHashMap<>();

    public boolean isOnCooldown(UUID playerId, String command, long cooldownMillis) {
        return remainingMillis(playerId, command, cooldownMillis) > 0;
    }

    public long remainingMillis(UUID playerId, String command, long cooldownMillis) {
        Map<String, Long> perPlayer = lastUse.get(playerId);
        if (perPlayer == null) {
            return 0;
        }
        Long last = perPlayer.get(command);
        if (last == null) {
            return 0;
        }
        long elapsed = System.currentTimeMillis() - last;
        return Math.max(0, cooldownMillis - elapsed);
    }

    public void markUsed(UUID playerId, String command) {
        lastUse.computeIfAbsent(playerId, key -> new ConcurrentHashMap<>()).put(command, System.currentTimeMillis());
    }
}
