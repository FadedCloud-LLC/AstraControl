package dev.aponder.astracontrol.hooks;

import dev.aponder.astracontrol.config.HooksConfig;

public final class ProtocolLibHook extends AbstractHook {

    public ProtocolLibHook(HooksConfig hooksConfig) {
        super("protocollib", "ProtocolLib", "ProtocolLib",
                "Detected for informational purposes only. AstraControl does not use packets.", hooksConfig);
    }
}
