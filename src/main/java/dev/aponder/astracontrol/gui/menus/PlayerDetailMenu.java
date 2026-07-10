package dev.aponder.astracontrol.gui.menus;

import dev.aponder.astracontrol.gui.BaseMenu;
import dev.aponder.astracontrol.gui.GuiContext;
import dev.aponder.astracontrol.gui.GuiLayout;
import dev.aponder.astracontrol.language.TranslationKey;
import dev.aponder.astracontrol.permissions.PermissionDebugManager;
import dev.aponder.astracontrol.placeholder.PlaceholderTester;
import dev.aponder.astracontrol.players.PlayerActionService;
import dev.aponder.astracontrol.text.MessageUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Detail/actions view for a single online player.
 */
public final class PlayerDetailMenu extends BaseMenu {

    public PlayerDetailMenu(GuiContext ctx,
                             Player target,
                             PlayerActionService actionService,
                             PermissionDebugManager permissionDebugManager,
                             PlaceholderTester placeholderTester) {
        super(GuiLayout.parse(ctx.guiConfig().menuSection("player-detail")));
        ConfigurationSection section = ctx.guiConfig().menuSection("player-detail");

        bindConfigButton(section, "teleport-to", "astracontrol.stafftools", ctx.language(), (staff, click) -> {
            actionService.teleportToPlayer(staff, target);
            staff.closeInventory();
        });

        bindConfigButton(section, "teleport-here", "astracontrol.stafftools", ctx.language(), (staff, click) -> {
            actionService.teleportPlayerHere(staff, target);
            staff.closeInventory();
        });

        bindConfigButton(section, "inventory", "astracontrol.stafftools", ctx.language(), (staff, click) -> {
            if (!actionService.openInventory(staff, target)) {
                ctx.language().send(staff, TranslationKey.GUI_NO_ACCESS_ITEM);
            }
        });

        bindConfigButton(section, "message", "astracontrol.stafftools", ctx.language(), (staff, click) -> {
            staff.closeInventory();
            ctx.guiManager().sessions().getOrCreate(staff.getUniqueId()).awaitChatInput(
                    "Enter the message to send to " + target.getName(),
                    message -> {
                        if (target.isOnline()) {
                            actionService.sendMessage(target, MessageUtil.render(message, Map.of()));
                        }
                    },
                    null);
        });

        bindConfigButton(section, "staff-command", "astracontrol.stafftools", ctx.language(),
                (staff, click) -> actionService.runConfiguredStaffCommand(target));

        bindConfigButton(section, "permissions", "astracontrol.debug.player", ctx.language(), (staff, click) -> {
            ctx.guiManager().sessions().getOrCreate(staff.getUniqueId()).setSelectedTargetPlayer(target.getUniqueId());
            ctx.guiManager().openMenu(staff, new PermissionDebugMenu(ctx, permissionDebugManager));
        });

        bindConfigButton(section, "placeholders", "astracontrol.papi", ctx.language(), (staff, click) -> {
            ctx.guiManager().sessions().getOrCreate(staff.getUniqueId()).setSelectedTargetPlayer(target.getUniqueId());
            ctx.guiManager().openMenu(staff, new PlaceholderTesterMenu(ctx, placeholderTester, staff));
        });

        bindBackButton(ctx, "player-list");
    }
}
