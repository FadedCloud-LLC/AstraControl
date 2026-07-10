package dev.aponder.astracontrol.commands.subcommands;

import dev.aponder.astracontrol.commands.BaseCommand;
import dev.aponder.astracontrol.commands.CommandContext;
import dev.aponder.astracontrol.language.LanguageManager;
import dev.aponder.astracontrol.language.TranslationKey;
import dev.aponder.astracontrol.permissions.PermissionDebugManager;
import dev.aponder.astracontrol.permissions.PermissionDebugResult;
import dev.aponder.astracontrol.scheduler.SchedulerAdapter;
import dev.aponder.astracontrol.text.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

/**
 * Backs {@code /astractrl debug player <player>},
 * {@code debug permission <player> <permission>}, and
 * {@code debug command <player> <command>}.
 */
public final class DebugCommand extends BaseCommand {

    private final PermissionDebugManager permissionDebugManager;
    private final SchedulerAdapter scheduler;

    public DebugCommand(LanguageManager language, PermissionDebugManager permissionDebugManager, SchedulerAdapter scheduler) {
        super(language);
        this.permissionDebugManager = permissionDebugManager;
        this.scheduler = scheduler;
    }

    @Override
    public String name() {
        return "debug";
    }

    @Override
    public String permission() {
        return "astracontrol.debug";
    }

    @Override
    public String usage() {
        return "/astractrl debug <player|permission|command> <player> [args]";
    }

    @Override
    public String description() {
        return "Debugging tools: player overview, permission checks, and command execution as a player.";
    }

    @Override
    public List<String> tabComplete(CommandContext ctx) {
        if (ctx.argCount() <= 1) {
            return List.of("player", "permission", "command");
        }
        if (ctx.argCount() == 2) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        return List.of();
    }

    @Override
    public void execute(CommandContext ctx) {
        String sub = ctx.arg(0);
        if (sub == null || ctx.arg(1) == null) {
            invalidUsage(ctx);
            return;
        }

        Player target = requireOnlinePlayer(ctx, ctx.arg(1));
        if (target == null) {
            return;
        }

        switch (sub.toLowerCase()) {
            case "player" -> debugPlayer(ctx, target);
            case "permission" -> debugPermission(ctx, target);
            case "command" -> debugCommand(ctx, target);
            default -> invalidUsage(ctx);
        }
    }

    private void debugPlayer(CommandContext ctx, Player target) {
        if (!ctx.sender().hasPermission("astracontrol.debug.player")) {
            language.send(ctx.sender(), TranslationKey.NO_PERMISSION);
            return;
        }
        PermissionDebugResult result = permissionDebugManager.debugPlayer(target);
        language.send(ctx.sender(), TranslationKey.DEBUG_PLAYER_HEADER, Map.of("player", target.getName()));
        MessageUtil.send(ctx.sender(), MessageUtil.render(
                "<gray>OP: <white>{op}\n<gray>Groups: <white>{groups}\n<gray>Resolver: <white>{resolver}",
                Map.of(
                        "op", result.op() ? "Yes" : "No",
                        "groups", result.groups().isEmpty() ? "None" : String.join(", ", result.groups()),
                        "resolver", result.resolverUsed()
                )));
    }

    private void debugPermission(CommandContext ctx, Player target) {
        if (!ctx.sender().hasPermission("astracontrol.debug.permission")) {
            language.send(ctx.sender(), TranslationKey.NO_PERMISSION);
            return;
        }
        if (ctx.arg(2) == null) {
            invalidUsage(ctx);
            return;
        }
        String permission = ctx.joinArgsFrom(2);
        PermissionDebugResult result = permissionDebugManager.debugPermission(target, permission);
        language.send(ctx.sender(), TranslationKey.DEBUG_PERMISSION_RESULT, Map.of(
                "player", target.getName(),
                "result", result.hasPermission() ? "has" : "does not have",
                "permission", permission
        ));
        if (result.source() != null) {
            MessageUtil.send(ctx.sender(), MessageUtil.render("<gray>Source: <white>{source}", Map.of("source", result.source())));
        }
    }

    private void debugCommand(CommandContext ctx, Player target) {
        if (!ctx.sender().hasPermission("astracontrol.debug.command")) {
            language.send(ctx.sender(), TranslationKey.NO_PERMISSION);
            return;
        }
        if (ctx.arg(2) == null) {
            invalidUsage(ctx);
            return;
        }
        String command = ctx.joinArgsFrom(2);
        scheduler.runNow(() -> target.performCommand(command));
        MessageUtil.send(ctx.sender(), MessageUtil.render(
                "<gray>Ran <white>/{command} <gray>as <white>{player}",
                Map.of("command", command, "player", target.getName())));
    }
}
