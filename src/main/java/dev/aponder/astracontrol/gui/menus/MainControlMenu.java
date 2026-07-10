package dev.aponder.astracontrol.gui.menus;

import dev.aponder.astracontrol.gui.BaseMenu;
import dev.aponder.astracontrol.gui.GuiContext;
import dev.aponder.astracontrol.gui.GuiLayout;
import dev.aponder.astracontrol.language.TranslationKey;
import dev.aponder.astracontrol.lifecycle.ReloadResult;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

/**
 * The central control-center menu, opened by {@code /astractrl} or
 * {@code /astractrl gui}.
 */
public final class MainControlMenu extends BaseMenu {

    public MainControlMenu(GuiContext ctx) {
        super(GuiLayout.parse(ctx.guiConfig().menuSection("main")));
        ConfigurationSection section = ctx.guiConfig().menuSection("main");

        bindConfigButton(section, "health", "astracontrol.health", ctx.language(),
                (player, click) -> ctx.guiManager().open(player, "health"));
        bindConfigButton(section, "plugins", "astracontrol.plugins", ctx.language(),
                (player, click) -> ctx.guiManager().open(player, "plugin-list"));
        bindConfigButton(section, "hooks", "astracontrol.hooks", ctx.language(),
                (player, click) -> ctx.guiManager().open(player, "hooks"));
        bindConfigButton(section, "players", "astracontrol.player", ctx.language(),
                (player, click) -> ctx.guiManager().open(player, "player-list"));
        bindConfigButton(section, "errors", "astracontrol.errors", ctx.language(),
                (player, click) -> ctx.guiManager().open(player, "errors"));
        bindConfigButton(section, "maintenance", "astracontrol.maintenance", ctx.language(),
                (player, click) -> ctx.guiManager().open(player, "maintenance"));
        bindConfigButton(section, "placeholders", "astracontrol.papi", ctx.language(),
                (player, click) -> ctx.guiManager().open(player, "placeholders"));
        bindConfigButton(section, "permissions", "astracontrol.debug", ctx.language(),
                (player, click) -> ctx.guiManager().open(player, "permission-debug"));
        bindConfigButton(section, "broadcast", "astracontrol.broadcast", ctx.language(),
                (player, click) -> ctx.guiManager().open(player, "broadcast"));
        bindConfigButton(section, "reload", "astracontrol.reload", ctx.language(),
                (player, click) -> {
                    ctx.language().send(player, TranslationKey.RELOAD_STARTED);
                    ReloadResult result = ctx.reloadManager().reload();
                    if (result.success()) {
                        ctx.language().send(player, TranslationKey.RELOAD_SUCCESS,
                                Map.of("time", String.valueOf(result.elapsedMillis())));
                    } else {
                        ctx.language().send(player, TranslationKey.RELOAD_FAILED,
                                Map.of("error", String.valueOf(result.errorMessage())));
                    }
                });
        bindConfigButton(section, "close", null, ctx.language(),
                (player, click) -> player.closeInventory());
    }
}
