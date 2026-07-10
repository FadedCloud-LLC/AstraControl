package dev.aponder.astracontrol.players;

import java.util.UUID;

/**
 * Read-only view of a single online player's current state, as shown in the Online
 * Player Tools GUI and {@code /astractrl playerinfo}.
 */
public record PlayerSnapshot(
        String name,
        UUID uniqueId,
        String worldName,
        double x,
        double y,
        double z,
        int ping,
        String gameMode,
        double health,
        int foodLevel,
        boolean op,
        String clientBrand
) {

    public boolean pingAvailable() {
        return ping >= 0;
    }

    public boolean clientBrandAvailable() {
        return clientBrand != null && !clientBrand.isBlank();
    }
}
