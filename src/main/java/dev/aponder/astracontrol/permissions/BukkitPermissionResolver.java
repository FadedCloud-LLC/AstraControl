package dev.aponder.astracontrol.permissions;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * Fallback permission resolver used when LuckPerms is not installed. Reports the
 * effective Bukkit permission result and OP status only - Bukkit's permission system
 * has no concept of "source" or "groups" on its own.
 */
public final class BukkitPermissionResolver {

    public PermissionDebugResult resolvePermission(Player player, String permission) {
        boolean has = player.hasPermission(permission);
        String source = player.isOp() ? "Operator" : (has ? "Effective permission (attachment/default)" : null);
        return new PermissionDebugResult(player.getName(), permission, has, source, List.of(), player.isOp(), "Bukkit");
    }

    public PermissionDebugResult resolvePlayer(Player player) {
        return new PermissionDebugResult(player.getName(), null, false, null, List.of(), player.isOp(), "Bukkit");
    }
}
