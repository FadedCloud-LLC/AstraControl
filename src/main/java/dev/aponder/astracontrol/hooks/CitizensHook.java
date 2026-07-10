package dev.aponder.astracontrol.hooks;

import dev.aponder.astracontrol.config.HooksConfig;

public final class CitizensHook extends AbstractHook {

    public CitizensHook(HooksConfig hooksConfig) {
        super("citizens", "Citizens", "Citizens", "Detected for informational purposes only.", hooksConfig);
    }
}
