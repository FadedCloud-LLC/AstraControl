package dev.aponder.astracontrol.text;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 * Safe bridge to PlaceholderAPI. Every call checks that PlaceholderAPI is installed
 * and enabled first, and never performs blocking I/O of its own - it only forwards to
 * PlaceholderAPI's own (synchronous, in-process) parser.
 */
public final class PlaceholderUtil {

    private PlaceholderUtil() {
    }

    public static boolean isAvailable() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    /**
     * Replaces every {@code %placeholder%} occurrence in {@code text} for the given
     * player. Returns {@code text} unchanged if PlaceholderAPI is not available.
     */
    public static String apply(OfflinePlayer player, String text) {
        if (!isAvailable() || text == null) {
            return text;
        }
        try {
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text);
        } catch (Exception e) {
            return text;
        }
    }

    /**
     * Resolves a single raw placeholder (e.g. {@code %player_name%}) for the given
     * player. Returns {@code null} if PlaceholderAPI is unavailable or the
     * placeholder could not be resolved.
     */
    public static String resolveSingle(OfflinePlayer player, String placeholder) {
        if (!isAvailable() || placeholder == null) {
            return null;
        }
        try {
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, placeholder);
        } catch (Exception e) {
            return null;
        }
    }
}
