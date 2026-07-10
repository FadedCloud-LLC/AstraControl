package dev.aponder.astracontrol.hooks;

import dev.aponder.astracontrol.config.HooksConfig;

public final class TabHook extends AbstractHook {

    public TabHook(HooksConfig hooksConfig) {
        super("tab", "TAB", "TAB", "Detected for informational purposes only.", hooksConfig);
    }
}
