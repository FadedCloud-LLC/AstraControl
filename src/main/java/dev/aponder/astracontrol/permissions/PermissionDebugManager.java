package dev.aponder.astracontrol.permissions;

import dev.aponder.astracontrol.config.ConfigManager;
import dev.aponder.astracontrol.logging.DebugLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

/**
 * Facade used by the permission debug command/GUI. Prefers LuckPerms when installed
 * and {@code permission-debugger.prefer-luckperms} is true, otherwise falls back to
 * plain Bukkit permission checks.
 */
public final class PermissionDebugManager {

    private final ConfigManager config;
    private final DebugLogger debugLogger;
    private final BukkitPermissionResolver bukkitResolver = new BukkitPermissionResolver();
    private final LuckPermsPermissionResolver luckPermsResolver = new LuckPermsPermissionResolver();

    public PermissionDebugManager(ConfigManager config, DebugLogger debugLogger) {
        this.config = config;
        this.debugLogger = debugLogger;
    }

    public PermissionDebugResult debugPermission(Player player, String permission) {
        return withLuckPermsFallback(
                () -> luckPermsResolver.resolvePermission(player, permission),
                () -> bukkitResolver.resolvePermission(player, permission));
    }

    public PermissionDebugResult debugPlayer(Player player) {
        return withLuckPermsFallback(
                () -> luckPermsResolver.resolvePlayer(player),
                () -> bukkitResolver.resolvePlayer(player));
    }

    private PermissionDebugResult withLuckPermsFallback(Supplier<PermissionDebugResult> luckPermsCall,
                                                          Supplier<PermissionDebugResult> bukkitFallback) {
        if (useLuckPerms()) {
            try {
                return luckPermsCall.get();
            } catch (Exception e) {
                debugLogger.debug("LuckPerms permission lookup failed, falling back to Bukkit", e);
            }
        }
        return bukkitFallback.get();
    }

    private boolean useLuckPerms() {
        return config.isPreferLuckPerms() && Bukkit.getPluginManager().isPluginEnabled("LuckPerms");
    }
}
