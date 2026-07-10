package dev.aponder.astracontrol.hooks;

import dev.aponder.astracontrol.config.HooksConfig;

public final class NexoHook extends AbstractHook {

    public NexoHook(HooksConfig hooksConfig) {
        super("nexo", "Nexo", "Nexo", "Detected for informational purposes only.", hooksConfig);
    }
}
