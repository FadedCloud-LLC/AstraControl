package dev.aponder.astracontrol.hooks;

import dev.aponder.astracontrol.config.HooksConfig;

public final class VaultHook extends AbstractHook {

    public VaultHook(HooksConfig hooksConfig) {
        super("vault", "Vault", "Vault", "Detected for economy/permission provider compatibility.", hooksConfig);
    }
}
