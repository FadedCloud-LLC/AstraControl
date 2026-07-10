package dev.aponder.astracontrol.commands.subcommands;

import dev.aponder.astracontrol.commands.BaseCommand;
import dev.aponder.astracontrol.commands.CommandContext;
import dev.aponder.astracontrol.gui.GUIManager;
import dev.aponder.astracontrol.gui.menus.PlayerDetailMenu;
import dev.aponder.astracontrol.language.LanguageManager;
import dev.aponder.astracontrol.language.TranslationKey;
import dev.aponder.astracontrol.permissions.PermissionDebugManager;
import dev.aponder.astracontrol.placeholder.PlaceholderTester;
import dev.aponder.astracontrol.players.PlayerActionService;
import dev.aponder.astracontrol.players.PlayerSnapshot;
import dev.aponder.astracontrol.players.PlayerToolsManager;
import dev.aponder.astracontrol.text.MessageUtil;
import dev.aponder.astracontrol.gui.GuiContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

/**
 * Backs {@code /astractrl player <player>} (brief info),
 * {@code /astractrl playerinfo <player>} (detailed snapshot), and
 * {@code /astractrl stafftools <player>} (opens the staff tools GUI) - one instance
 * per top-level command name, each configured with its {@link Mode}.
 */
public final class PlayerCommand extends BaseCommand {

    public enum Mode {
        BASIC, DETAILED, STAFFTOOLS
    }

    private final PlayerToolsManager playerTools;
    private final PlayerActionService actionService;
    private final PermissionDebugManager permissionDebugManager;
    private final PlaceholderTester placeholderTester;
    private final GUIManager guiManager;
    private final GuiContext guiContext;
    private final Mode mode;
    private final String commandName;

    public PlayerCommand(LanguageManager language, PlayerToolsManager playerTools, PlayerActionService actionService,
                          PermissionDebugManager permissionDebugManager, PlaceholderTester placeholderTester,
                          GUIManager guiManager, GuiContext guiContext, Mode mode, String commandName) {
        super(language);
        this.playerTools = playerTools;
        this.actionService = actionService;
        this.permissionDebugManager = permissionDebugManager;
        this.placeholderTester = placeholderTester;
        this.guiManager = guiManager;
        this.guiContext = guiContext;
        this.mode = mode;
        this.commandName = commandName;
    }

    @Override
    public String name() {
        return commandName;
    }

    @Override
    public String permission() {
        return mode == Mode.STAFFTOOLS ? "astracontrol.stafftools" : "astracontrol.player";
    }

    @Override
    public String usage() {
        return "/astractrl " + commandName + " <player>";
    }

    @Override
    public String description() {
        return switch (mode) {
            case BASIC -> "Shows brief info about a player.";
            case DETAILED -> "Shows a detailed player snapshot.";
            case STAFFTOOLS -> "Opens the staff tools menu for a player.";
        };
    }

    @Override
    public List<String> tabComplete(CommandContext ctx) {
        if (ctx.argCount() <= 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        return List.of();
    }

    @Override
    public void execute(CommandContext ctx) {
        if (ctx.arg(0) == null) {
            invalidUsage(ctx);
            return;
        }
        Player target = requireOnlinePlayer(ctx, ctx.arg(0));
        if (target == null) {
            return;
        }

        if (mode == Mode.STAFFTOOLS) {
            Player staff = requirePlayer(ctx);
            if (staff == null) {
                return;
            }
            guiManager.openMenu(staff, new PlayerDetailMenu(guiContext, target, actionService, permissionDebugManager, placeholderTester));
            return;
        }

        PlayerSnapshot snapshot = playerTools.snapshot(target);
        if (mode == Mode.BASIC) {
            MessageUtil.send(ctx.sender(), MessageUtil.render(
                    "<gray>[<gold>AstraControl</gold><gray>] <white>{name}\n"
                            + "<gray>World: <white>{world} <gray>| Gamemode: <white>{gamemode}",
                    Map.of("name", snapshot.name(), "world", snapshot.worldName(), "gamemode", snapshot.gameMode())));
            return;
        }

        MessageUtil.send(ctx.sender(), MessageUtil.render(
                "<gray>[<gold>AstraControl</gold><gray>] <white>{name}\n"
                        + "<gray>UUID: <white>{uuid}\n"
                        + "<gray>World: <white>{world} <gray>@ <white>{x}, {y}, {z}\n"
                        + "<gray>Ping: <white>{ping}\n"
                        + "<gray>Gamemode: <white>{gamemode} <gray>| Health: <white>{health} <gray>| Food: <white>{food}\n"
                        + "<gray>OP: <white>{op}\n"
                        + "<gray>Client brand: <white>{brand}",
                Map.ofEntries(
                        Map.entry("name", snapshot.name()),
                        Map.entry("uuid", snapshot.uniqueId().toString()),
                        Map.entry("world", snapshot.worldName()),
                        Map.entry("x", String.format("%.1f", snapshot.x())),
                        Map.entry("y", String.format("%.1f", snapshot.y())),
                        Map.entry("z", String.format("%.1f", snapshot.z())),
                        Map.entry("ping", snapshot.pingAvailable() ? String.valueOf(snapshot.ping()) : "Unavailable"),
                        Map.entry("gamemode", snapshot.gameMode()),
                        Map.entry("health", String.format("%.1f", snapshot.health())),
                        Map.entry("food", String.valueOf(snapshot.foodLevel())),
                        Map.entry("op", snapshot.op() ? "Yes" : "No"),
                        Map.entry("brand", snapshot.clientBrandAvailable() ? snapshot.clientBrand() : "Unavailable")
                )));
    }
}
