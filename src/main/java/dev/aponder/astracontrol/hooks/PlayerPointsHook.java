package dev.aponder.astracontrol.hooks;

import dev.aponder.astracontrol.config.HooksConfig;

public final class PlayerPointsHook extends AbstractHook {

    public PlayerPointsHook(HooksConfig hooksConfig) {
        super("playerpoints", "PlayerPoints", "PlayerPoints", "Detected for informational purposes only.", hooksConfig);
    }
}
