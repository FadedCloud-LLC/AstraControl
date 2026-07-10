package dev.aponder.astracontrol.hooks;

import dev.aponder.astracontrol.config.HooksConfig;

public final class ItemsAdderHook extends AbstractHook {

    public ItemsAdderHook(HooksConfig hooksConfig) {
        super("itemsadder", "ItemsAdder", "ItemsAdder", "Detected for informational purposes only.", hooksConfig);
    }
}
