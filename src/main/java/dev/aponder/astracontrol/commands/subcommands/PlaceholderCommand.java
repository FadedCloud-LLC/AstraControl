package dev.aponder.astracontrol.commands.subcommands;

import dev.aponder.astracontrol.commands.BaseCommand;
import dev.aponder.astracontrol.commands.CommandContext;
import dev.aponder.astracontrol.language.LanguageManager;
import dev.aponder.astracontrol.language.TranslationKey;
import dev.aponder.astracontrol.placeholder.PlaceholderResult;
import dev.aponder.astracontrol.placeholder.PlaceholderTester;
import dev.aponder.astracontrol.text.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

/**
 * Backs {@code /astractrl papi <player> <placeholder>},
 * {@code papi raw <player> <placeholder>}, {@code papi render <player> <text>}, and
 * {@code papi compare <player> <placeholder>}.
 */
public final class PlaceholderCommand extends BaseCommand {

    private static final List<String> SUBCOMMANDS = List.of("raw", "render", "compare");

    private final PlaceholderTester tester;

    public PlaceholderCommand(LanguageManager language, PlaceholderTester tester) {
        super(language);
        this.tester = tester;
    }

    @Override
    public String name() {
        return "papi";
    }

    @Override
    public String permission() {
        return "astracontrol.papi";
    }

    @Override
    public String usage() {
        return "/astractrl papi [raw|render|compare] <player> <placeholder|text>";
    }

    @Override
    public String description() {
        return "Tests a PlaceholderAPI placeholder or block of text against a player.";
    }

    @Override
    public List<String> tabComplete(CommandContext ctx) {
        if (ctx.argCount() <= 1) {
            List<String> options = new java.util.ArrayList<>(SUBCOMMANDS);
            Bukkit.getOnlinePlayers().forEach(p -> options.add(p.getName()));
            return options;
        }
        if (ctx.argCount() == 2 && SUBCOMMANDS.contains(ctx.arg(0).toLowerCase())) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        return List.of();
    }

    @Override
    public void execute(CommandContext ctx) {
        String first = ctx.arg(0);
        if (first == null) {
            invalidUsage(ctx);
            return;
        }

        String subcommand = SUBCOMMANDS.contains(first.toLowerCase()) ? first.toLowerCase() : null;
        int playerIndex = subcommand == null ? 0 : 1;
        int inputIndex = playerIndex + 1;

        String playerName = ctx.arg(playerIndex);
        if (playerName == null || ctx.arg(inputIndex) == null) {
            invalidUsage(ctx);
            return;
        }

        Player target = requireOnlinePlayer(ctx, playerName);
        if (target == null) {
            return;
        }

        String input = ctx.joinArgsFrom(inputIndex);

        if ("render".equals(subcommand)) {
            PlaceholderResult result = tester.renderText(target, input);
            sendResult(ctx, result, true);
            return;
        }

        PlaceholderResult result = tester.testPlaceholder(target, input);

        if ("compare".equals(subcommand)) {
            if (!result.placeholderApiAvailable()) {
                language.send(ctx.sender(), TranslationKey.PLACEHOLDERAPI_MISSING);
                return;
            }
            MessageUtil.send(ctx.sender(), MessageUtil.render(
                    "<gray>Input: <white>{input}\n<gray>Resolved: <white>{resolved}",
                    Map.of("input", input, "resolved", result.resolved() ? result.raw() : "No output")));
            return;
        }

        sendResult(ctx, result, "raw".equals(subcommand));
    }

    private void sendResult(CommandContext ctx, PlaceholderResult result, boolean rawOnly) {
        if (!result.placeholderApiAvailable()) {
            language.send(ctx.sender(), TranslationKey.PLACEHOLDERAPI_MISSING);
            return;
        }
        if (!result.resolved()) {
            language.send(ctx.sender(), TranslationKey.PLACEHOLDER_MISSING);
            return;
        }

        if (rawOnly) {
            MessageUtil.send(ctx.sender(), MessageUtil.render("<gray>Raw: <white>{raw}", Map.of("raw", result.raw())));
            return;
        }

        MessageUtil.send(ctx.sender(), MessageUtil.render(
                "<gray>Raw: <white>{raw}\n<gray>Plain: <white>{plain}\n<gray>MiniMessage: <white>{minimessage}\n<gray>Legacy: <white>{legacy}",
                Map.of(
                        "raw", result.raw(),
                        "plain", result.plain(),
                        "minimessage", result.miniMessage(),
                        "legacy", result.legacy()
                )));
    }
}
