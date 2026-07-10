package dev.aponder.astracontrol.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Central MiniMessage rendering utility. Placeholder values are always inserted as
 * unparsed literal text so untrusted input (player names, chat messages, etc.) can
 * never inject additional MiniMessage tags into a template.
 */
public final class MessageUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private MessageUtil() {
    }

    public static Component render(String template, Map<String, String> placeholders) {
        if (template == null) {
            return Component.empty();
        }
        return MINI_MESSAGE.deserialize(template, resolvers(placeholders));
    }

    public static List<Component> renderLines(List<String> templates, Map<String, String> placeholders) {
        List<Component> lines = new ArrayList<>(templates.size());
        for (String template : templates) {
            lines.add(render(template, placeholders));
        }
        return lines;
    }

    public static void send(CommandSender target, Component component) {
        target.sendMessage(component);
    }

    public static void send(CommandSender target, String template, Map<String, String> placeholders) {
        send(target, render(template, placeholders));
    }

    public static MiniMessage miniMessage() {
        return MINI_MESSAGE;
    }

    /**
     * Like {@link #render(String, Map)}, but values in {@code parsedPlaceholders} are
     * inserted as MiniMessage (so the caller's own tags render), while
     * {@code unparsedPlaceholders} stay literal. Only use {@code parsedPlaceholders}
     * for admin-supplied input (e.g. a broadcast message), never for arbitrary player
     * data.
     */
    public static Component renderMixed(String template,
                                         Map<String, String> unparsedPlaceholders,
                                         Map<String, String> parsedPlaceholders) {
        if (template == null) {
            return Component.empty();
        }
        TagResolver.Builder builder = TagResolver.builder();
        unparsedPlaceholders.forEach((key, value) -> builder.resolver(Placeholder.unparsed(key, value == null ? "" : value)));
        parsedPlaceholders.forEach((key, value) -> builder.resolver(Placeholder.parsed(key, value == null ? "" : value)));
        return MINI_MESSAGE.deserialize(template, builder.build());
    }

    private static TagResolver resolvers(Map<String, String> placeholders) {
        if (placeholders.isEmpty()) {
            return TagResolver.empty();
        }
        TagResolver.Builder builder = TagResolver.builder();
        placeholders.forEach((key, value) -> builder.resolver(Placeholder.unparsed(key, value == null ? "" : value)));
        return builder.build();
    }
}
