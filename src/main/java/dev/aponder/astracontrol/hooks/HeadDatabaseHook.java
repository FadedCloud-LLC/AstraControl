package dev.aponder.astracontrol.hooks;

import dev.aponder.astracontrol.config.HooksConfig;

public final class HeadDatabaseHook extends AbstractHook {

    public HeadDatabaseHook(HooksConfig hooksConfig) {
        super("headdatabase", "HeadDatabase", "HeadDatabase", "Detected for informational purposes only.", hooksConfig);
    }
}
