package dev.aponder.astracontrol.gui.menus;

import dev.aponder.astracontrol.gui.BaseMenu;
import dev.aponder.astracontrol.gui.GuiContext;
import dev.aponder.astracontrol.gui.GuiLayout;
import dev.aponder.astracontrol.gui.MenuSession;
import dev.aponder.astracontrol.language.TranslationKey;
import dev.aponder.astracontrol.placeholder.PlaceholderResult;
import dev.aponder.astracontrol.placeholder.PlaceholderTester;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Lets staff pick a player, test a single placeholder or a block of text against
 * them, and view the raw/plain/MiniMessage/legacy representations of the result.
 */
public final class PlaceholderTesterMenu extends BaseMenu {

    public PlaceholderTesterMenu(GuiContext ctx, PlaceholderTester tester, Player viewer) {
        this(ctx, tester, viewer, null);
    }

    public PlaceholderTesterMenu(GuiContext ctx, PlaceholderTester tester, Player viewer, PlaceholderResult lastResult) {
        super(GuiLayout.parse(ctx.guiConfig().menuSection("placeholders")));
        ConfigurationSection section = ctx.guiConfig().menuSection("placeholders");

        MenuSession session = ctx.guiManager().sessions().getOrCreate(viewer.getUniqueId());
        OfflinePlayer selected = session.selectedTargetPlayer() != null
                ? Bukkit.getOfflinePlayer(session.selectedTargetPlayer())
                : null;

        bindConfigButton(section, "select-player", "astracontrol.papi", ctx.language(),
                Map.of("selected", selected != null && selected.getName() != null ? selected.getName() : "None"),
                (player, click) -> {
                    player.closeInventory();
                    session.awaitChatInput("Type a player name", name -> {
                        Player found = Bukkit.getPlayerExact(name);
                        if (found == null) {
                            ctx.language().send(player, TranslationKey.PLAYER_NOT_FOUND, Map.of("player", name));
                        } else {
                            session.setSelectedTargetPlayer(found.getUniqueId());
                        }
                        ctx.guiManager().openMenu(player, new PlaceholderTesterMenu(ctx, tester, player, lastResult));
                    }, () -> ctx.guiManager().openMenu(player, new PlaceholderTesterMenu(ctx, tester, player, lastResult)));
                });

        bindConfigButton(section, "enter-placeholder", "astracontrol.papi", ctx.language(), (player, click) -> {
            if (selected == null) {
                ctx.language().send(player, TranslationKey.PLAYER_NOT_FOUND, Map.of("player", "?"));
                return;
            }
            player.closeInventory();
            session.awaitChatInput("Type a placeholder (e.g. %player_name%)", input -> {
                PlaceholderResult result = tester.testPlaceholder(selected, input);
                ctx.guiManager().openMenu(player, new PlaceholderTesterMenu(ctx, tester, player, result));
            }, () -> ctx.guiManager().openMenu(player, new PlaceholderTesterMenu(ctx, tester, player, lastResult)));
        });

        String rawDisplay = describeUnavailable(lastResult);
        bindDecoration(section, "view-raw",
                Map.of("raw", lastResult != null && lastResult.resolved() ? safe(lastResult.raw()) : rawDisplay));

        bindDecoration(section, "view-rendered", Map.of(
                "plain", lastResult != null && lastResult.resolved() ? safe(lastResult.plain()) : rawDisplay,
                "minimessage", lastResult != null && lastResult.resolved() ? safe(lastResult.miniMessage()) : rawDisplay,
                "legacy", lastResult != null && lastResult.resolved() ? safe(lastResult.legacy()) : rawDisplay
        ));

        bindBackButton(ctx, "main");
    }

    private String describeUnavailable(PlaceholderResult result) {
        if (result == null) {
            return "No test run yet";
        }
        if (!result.placeholderApiAvailable()) {
            return "PlaceholderAPI is not installed";
        }
        return "Placeholder produced no output";
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
