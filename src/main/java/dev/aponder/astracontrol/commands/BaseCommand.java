package dev.aponder.astracontrol.commands;

import dev.aponder.astracontrol.language.LanguageManager;
import dev.aponder.astracontrol.language.TranslationKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Common convenience methods (usage/permission/player-only messaging) shared by
 * every concrete subcommand.
 */
public abstract class BaseCommand implements SubCommand {

    protected final LanguageManager language;

    protected BaseCommand(LanguageManager language) {
        this.language = language;
    }

    protected void invalidUsage(CommandContext ctx) {
        language.send(ctx.sender(), TranslationKey.INVALID_USAGE, Map.of("usage", usage()));
    }

    protected void playerOnly(CommandContext ctx) {
        language.send(ctx.sender(), TranslationKey.PLAYER_ONLY);
    }

    protected Player requirePlayer(CommandContext ctx) {
        Player player = ctx.asPlayer();
        if (player == null) {
            playerOnly(ctx);
        }
        return player;
    }

    protected Player requireOnlinePlayer(CommandContext ctx, String name) {
        Player target = Bukkit.getPlayerExact(name);
        if (target == null) {
            language.send(ctx.sender(), TranslationKey.PLAYER_NOT_FOUND, Map.of("player", name));
        }
        return target;
    }
}
