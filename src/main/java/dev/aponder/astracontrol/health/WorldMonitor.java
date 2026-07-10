package dev.aponder.astracontrol.health;

import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 * Reads loaded world and chunk counts using only safe, public Bukkit API.
 */
public final class WorldMonitor {

    public int loadedWorldCount() {
        return Bukkit.getWorlds().size();
    }

    public int loadedChunkCount() {
        int total = 0;
        for (World world : Bukkit.getWorlds()) {
            total += world.getLoadedChunks().length;
        }
        return total;
    }
}
