package dev.aponder.astracontrol.hooks;

import dev.aponder.astracontrol.config.HooksConfig;

public final class EssentialsHook extends AbstractHook {

    public EssentialsHook(HooksConfig hooksConfig) {
        super("essentialsx", "EssentialsX", "Essentials",
                "Detected for informational purposes only. AstraControl is not an EssentialsX replacement.", hooksConfig);
    }
}
