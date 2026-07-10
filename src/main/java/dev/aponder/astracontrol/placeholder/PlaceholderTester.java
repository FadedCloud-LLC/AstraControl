package dev.aponder.astracontrol.placeholder;

import dev.aponder.astracontrol.text.ColorUtil;
import dev.aponder.astracontrol.text.PlaceholderUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;

/**
 * Resolves a single placeholder (or a block of text containing several) through
 * PlaceholderAPI and exposes the result in raw, plain, MiniMessage, and legacy form.
 * Never performs HTTP/SQL and never crashes - every failure degrades to an
 * "unavailable"/"unresolved" result.
 */
public final class PlaceholderTester {

    public PlaceholderResult testPlaceholder(OfflinePlayer player, String placeholder) {
        if (!PlaceholderUtil.isAvailable()) {
            return PlaceholderResult.unavailable(placeholder);
        }
        String raw = PlaceholderUtil.resolveSingle(player, placeholder);
        if (raw == null || raw.equals(placeholder)) {
            return PlaceholderResult.unresolved(placeholder);
        }
        return buildResult(placeholder, raw);
    }

    public PlaceholderResult renderText(OfflinePlayer player, String text) {
        if (!PlaceholderUtil.isAvailable()) {
            return PlaceholderResult.unavailable(text);
        }
        String raw = PlaceholderUtil.apply(player, text);
        if (raw == null) {
            return PlaceholderResult.unresolved(text);
        }
        return buildResult(text, raw);
    }

    private PlaceholderResult buildResult(String input, String raw) {
        Component component = ColorUtil.fromLegacy(raw);
        return new PlaceholderResult(
                input,
                true,
                true,
                raw,
                ColorUtil.toPlain(component),
                ColorUtil.toMiniMessage(component),
                ColorUtil.toLegacy(component)
        );
    }
}
