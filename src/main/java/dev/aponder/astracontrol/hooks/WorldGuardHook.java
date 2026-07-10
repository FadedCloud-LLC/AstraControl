package dev.aponder.astracontrol.hooks;

import dev.aponder.astracontrol.config.HooksConfig;

public final class WorldGuardHook extends AbstractHook {

    public WorldGuardHook(HooksConfig hooksConfig) {
        super("worldguard", "WorldGuard", "WorldGuard", "Detected for informational purposes only.", hooksConfig);
    }
}
