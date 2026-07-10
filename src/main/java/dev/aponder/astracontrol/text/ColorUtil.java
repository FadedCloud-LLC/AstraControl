package dev.aponder.astracontrol.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

/**
 * Conversions between MiniMessage, legacy '&'-code text, and plain text. Used mainly
 * by the placeholder tester, which needs to show a player every representation of a
 * rendered value.
 */
public final class ColorUtil {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();
    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private ColorUtil() {
    }

    public static String toLegacy(Component component) {
        return LEGACY.serialize(component);
    }

    public static String toPlain(Component component) {
        return PLAIN.serialize(component);
    }

    public static String toMiniMessage(Component component) {
        return MINI_MESSAGE.serialize(component);
    }

    public static Component fromLegacy(String legacyText) {
        return LEGACY.deserialize(legacyText);
    }
}
