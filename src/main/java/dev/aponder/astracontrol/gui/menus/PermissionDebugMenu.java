package dev.aponder.astracontrol.gui.menus;

import dev.aponder.astracontrol.gui.BaseMenu;
import dev.aponder.astracontrol.gui.GuiContext;
import dev.aponder.astracontrol.gui.GuiLayout;
import dev.aponder.astracontrol.gui.MenuSession;
import dev.aponder.astracontrol.language.TranslationKey;
import dev.aponder.astracontrol.permissions.PermissionDebugManager;
import dev.aponder.astracontrol.permissions.PermissionDebugResult;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Lets staff pick an online player and either debug a specific permission node or
 * view their overall permission groups.
 */
public final class PermissionDebugMenu extends BaseMenu {

    public PermissionDebugMenu(GuiContext ctx, PermissionDebugManager permissionDebugManager) {
        this(ctx, permissionDebugManager, null);
    }

    public PermissionDebugMenu(GuiContext ctx, PermissionDebugManager permissionDebugManager, PermissionDebugResult lastResult) {
        super(GuiLayout.parse(ctx.guiConfig().menuSection("permission-debug")));
        ConfigurationSection section = ctx.guiConfig().menuSection("permission-debug");

        bindConfigButton(section, "select-player", "astracontrol.debug", ctx.language(),
                Map.of("selected", lastResult != null && lastResult.playerName() != null ? lastResult.playerName() : "None"),
                (player, click) -> {
                    MenuSession session = ctx.guiManager().sessions().getOrCreate(player.getUniqueId());
                    player.closeInventory();
                    session.awaitChatInput("Type a player name", name -> {
                        Player found = Bukkit.getPlayerExact(name);
                        if (found == null) {
                            ctx.language().send(player, TranslationKey.PLAYER_NOT_FOUND, Map.of("player", name));
                        } else {
                            session.setSelectedTargetPlayer(found.getUniqueId());
                        }
                        ctx.guiManager().openMenu(player, new PermissionDebugMenu(ctx, permissionDebugManager, lastResult));
                    }, () -> ctx.guiManager().openMenu(player, new PermissionDebugMenu(ctx, permissionDebugManager, lastResult)));
                });

        bindConfigButton(section, "enter-permission", "astracontrol.debug.permission", ctx.language(),
                (player, click) -> {
                    MenuSession session = ctx.guiManager().sessions().getOrCreate(player.getUniqueId());
                    Player target = resolveSelected(session);
                    if (target == null) {
                        return;
                    }
                    player.closeInventory();
                    session.awaitChatInput("Type a permission node", permission -> {
                        PermissionDebugResult result = permissionDebugManager.debugPermission(target, permission);
                        ctx.guiManager().openMenu(player, new PermissionDebugMenu(ctx, permissionDebugManager, result));
                    }, () -> ctx.guiManager().openMenu(player, new PermissionDebugMenu(ctx, permissionDebugManager, lastResult)));
                });

        bindDecoration(section, "view-result", Map.of(
                "permission", lastResult != null && lastResult.permission() != null ? lastResult.permission() : "-",
                "result", lastResult != null && lastResult.permission() != null
                        ? (lastResult.hasPermission() ? "Granted" : "Denied")
                        : "No lookup yet",
                "source", lastResult != null && lastResult.source() != null ? lastResult.source() : "Unknown",
                "resolver", lastResult != null ? lastResult.resolverUsed() : "-"
        ));

        bindConfigButton(section, "view-groups", "astracontrol.debug.player", ctx.language(),
                Map.of("groups", lastResult != null && !lastResult.groups().isEmpty()
                        ? String.join(", ", lastResult.groups())
                        : "None"),
                (player, click) -> {
                    MenuSession session = ctx.guiManager().sessions().getOrCreate(player.getUniqueId());
                    Player target = resolveSelected(session);
                    if (target == null) {
                        return;
                    }
                    PermissionDebugResult result = permissionDebugManager.debugPlayer(target);
                    ctx.guiManager().openMenu(player, new PermissionDebugMenu(ctx, permissionDebugManager, result));
                });

        bindBackButton(ctx, "main");
    }

    private Player resolveSelected(MenuSession session) {
        if (session.selectedTargetPlayer() == null) {
            return null;
        }
        return Bukkit.getPlayer(session.selectedTargetPlayer());
    }
}
