package dev.aponder.astracontrol.permissions;

import java.util.List;

/**
 * Result of a permission or player debug lookup. {@code permission} and
 * {@code hasPermission} are only meaningful when a specific permission was checked
 * ({@code /astractrl debug permission}); for a general player debug
 * ({@code /astractrl debug player}) they are {@code null}/{@code false} and only
 * {@code groups}/{@code op} are populated.
 */
public record PermissionDebugResult(
        String playerName,
        String permission,
        boolean hasPermission,
        String source,
        List<String> groups,
        boolean op,
        String resolverUsed
) {
}
