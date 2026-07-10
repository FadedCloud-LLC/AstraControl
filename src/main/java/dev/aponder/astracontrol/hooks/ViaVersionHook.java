package dev.aponder.astracontrol.hooks;

import dev.aponder.astracontrol.config.HooksConfig;

public final class ViaVersionHook extends AbstractHook {

    public ViaVersionHook(HooksConfig hooksConfig) {
        super("viaversion", "ViaVersion", "ViaVersion", "Detected for informational purposes only.", hooksConfig);
    }
}
