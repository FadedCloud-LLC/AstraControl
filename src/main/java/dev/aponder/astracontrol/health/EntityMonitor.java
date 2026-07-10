package dev.aponder.astracontrol.health;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

/**
 * Reads entity and tile-entity counts. Aggregating across every loaded chunk in every
 * world is inherently a cross-region operation on Folia, so every read here is
 * defensively wrapped: if the platform refuses the read (or any other error occurs),
 * this reports "unavailable" (-1) instead of throwing.
 */
public final class EntityMonitor {

    public int entityCount() {
        try {
            int total = 0;
            for (World world : Bukkit.getWorlds()) {
                total += world.getEntities().size();
            }
            return total;
        } catch (RuntimeException e) {
            return -1;
        }
    }

    public int tileEntityCount() {
        try {
            int total = 0;
            for (World world : Bukkit.getWorlds()) {
                for (Chunk chunk : world.getLoadedChunks()) {
                    total += chunk.getTileEntities().length;
                }
            }
            return total;
        } catch (RuntimeException e) {
            return -1;
        }
    }
}
