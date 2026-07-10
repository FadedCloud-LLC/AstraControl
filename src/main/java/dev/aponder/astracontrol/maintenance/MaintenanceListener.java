package dev.aponder.astracontrol.maintenance;

import dev.aponder.astracontrol.config.ConfigManager;
import dev.aponder.astracontrol.language.LanguageManager;
import dev.aponder.astracontrol.language.TranslationKey;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.Map;

/**
 * Denies logins while maintenance mode is enabled, unless the joining player holds
 * the configured bypass permission. Runs at {@link EventPriority#LOWEST} so
 * maintenance is enforced before other plugins act on the login.
 */
public final class MaintenanceListener implements Listener {

    private final MaintenanceManager maintenanceManager;
    private final ConfigManager config;
    private final LanguageManager language;

    public MaintenanceListener(MaintenanceManager maintenanceManager, ConfigManager config, LanguageManager language) {
        this.maintenanceManager = maintenanceManager;
        this.config = config;
        this.language = language;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent event) {
        if (!maintenanceManager.isEnabled()) {
            return;
        }

        Player player = event.getPlayer();
        if (player.hasPermission(config.getMaintenanceBypassPermission())) {
            return;
        }

        Component kickMessage = language.render(
                TranslationKey.MAINTENANCE_JOIN_DENIED,
                Map.of("reason", maintenanceManager.reason()));
        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, kickMessage);
    }
}
