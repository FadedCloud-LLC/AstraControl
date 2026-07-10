package dev.aponder.astracontrol.gui.menus;

import dev.aponder.astracontrol.broadcast.BroadcastManager;
import dev.aponder.astracontrol.gui.BaseMenu;
import dev.aponder.astracontrol.gui.GuiContext;
import dev.aponder.astracontrol.gui.GuiLayout;
import dev.aponder.astracontrol.gui.MenuSession;
import dev.aponder.astracontrol.language.TranslationKey;
import dev.aponder.astracontrol.maintenance.MaintenanceManager;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

/**
 * Toggle and configure maintenance mode.
 */
public final class MaintenanceMenu extends BaseMenu {

    public MaintenanceMenu(GuiContext ctx, MaintenanceManager maintenanceManager, BroadcastManager broadcastManager) {
        super(GuiLayout.parse(ctx.guiConfig().menuSection("maintenance")));
        ConfigurationSection section = ctx.guiConfig().menuSection("maintenance");

        bindConfigButton(section, "toggle", "astracontrol.maintenance", ctx.language(),
                Map.of("state", maintenanceManager.isEnabled() ? "Enabled" : "Disabled"),
                (player, click) -> {
                    if (maintenanceManager.isEnabled()) {
                        maintenanceManager.disable();
                        ctx.language().send(player, TranslationKey.MAINTENANCE_DISABLED);
                    } else {
                        maintenanceManager.enable(null);
                        ctx.language().send(player, TranslationKey.MAINTENANCE_ENABLED);
                    }
                    if (ctx.config().isMaintenanceBroadcastOnToggle()) {
                        broadcastManager.broadcastChat(maintenanceManager.isEnabled()
                                ? "<gold>Maintenance mode is now <green>enabled</green>."
                                : "<gold>Maintenance mode is now <red>disabled</red>.");
                    }
                    ctx.guiManager().openMenu(player, new MaintenanceMenu(ctx, maintenanceManager, broadcastManager));
                });

        bindConfigButton(section, "set-reason", "astracontrol.maintenance", ctx.language(),
                Map.of("reason", maintenanceManager.reason()),
                (player, click) -> {
                    player.closeInventory();
                    MenuSession session = ctx.guiManager().sessions().getOrCreate(player.getUniqueId());
                    session.awaitChatInput("Type the new maintenance reason", reason -> {
                        maintenanceManager.setReason(reason);
                        ctx.guiManager().openMenu(player, new MaintenanceMenu(ctx, maintenanceManager, broadcastManager));
                    }, () -> ctx.guiManager().openMenu(player, new MaintenanceMenu(ctx, maintenanceManager, broadcastManager)));
                });

        bindConfigButton(section, "broadcast", "astracontrol.broadcast", ctx.language(),
                (player, click) -> {
                    ctx.language().send(player, TranslationKey.MAINTENANCE_STATUS,
                            Map.of("state", maintenanceManager.isEnabled() ? "Enabled" : "Disabled"));
                    broadcastManager.broadcastChat("<gold>Maintenance mode is currently <white>"
                            + (maintenanceManager.isEnabled() ? "enabled" : "disabled") + "</white>.");
                });

        bindBackButton(ctx, "main");
    }
}
