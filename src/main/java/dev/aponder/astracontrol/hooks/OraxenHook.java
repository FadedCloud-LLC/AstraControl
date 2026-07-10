package dev.aponder.astracontrol.hooks;

import dev.aponder.astracontrol.config.HooksConfig;

public final class OraxenHook extends AbstractHook {

    public OraxenHook(HooksConfig hooksConfig) {
        super("oraxen", "Oraxen", "Oraxen", "Detected for informational purposes only.", hooksConfig);
    }
}
