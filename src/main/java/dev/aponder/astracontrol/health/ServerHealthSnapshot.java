package dev.aponder.astracontrol.health;

import java.time.Duration;

/**
 * Immutable point-in-time view of server health, as shown in the Health GUI menu and
 * returned by {@code /astractrl health}.
 */
public record ServerHealthSnapshot(
        double[] tps,
        double mspt,
        boolean msptAvailable,
        long usedMemoryBytes,
        long freeMemoryBytes,
        long maxMemoryBytes,
        Duration uptime,
        int onlinePlayers,
        int loadedWorlds,
        int loadedChunks,
        int entityCount,
        int tileEntityCount,
        String serverVersion,
        String javaVersion,
        String platformName,
        boolean folia
) {

    public double currentTps() {
        return tps.length > 0 ? tps[0] : -1;
    }

    public boolean tpsAvailable() {
        return tps.length > 0;
    }
}
