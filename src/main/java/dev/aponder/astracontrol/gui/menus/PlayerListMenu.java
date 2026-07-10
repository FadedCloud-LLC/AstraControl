package dev.aponder.astracontrol.gui.menus;

import dev.aponder.astracontrol.gui.GuiContext;
import dev.aponder.astracontrol.gui.GuiItem;
import dev.aponder.astracontrol.gui.GuiLayout;
import dev.aponder.astracontrol.gui.PaginatedMenu;
import dev.aponder.astracontrol.items.ItemBuilder;
import dev.aponder.astracontrol.items.ItemMetaUtil;
import dev.aponder.astracontrol.permissions.PermissionDebugManager;
import dev.aponder.astracontrol.placeholder.PlaceholderTester;
import dev.aponder.astracontrol.players.PlayerActionService;
import dev.aponder.astracontrol.players.PlayerToolsManager;
import dev.aponder.astracontrol.text.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.Map;

/**
 * Paginated list of every online player.
 */
public final class PlayerListMenu extends PaginatedMenu {

    public PlayerListMenu(GuiContext ctx,
                           PlayerToolsManager playerTools,
                           PlayerActionService actionService,
                           PermissionDebugManager permissionDebugManager,
                           PlaceholderTester placeholderTester) {
        super(GuiLayout.parse(ctx.guiConfig().menuSection("player-list")));

        List<GuiItem> content = playerTools.onlinePlayers().stream()
                .map(player -> toItem(ctx, player, playerTools, actionService, permissionDebugManager, placeholderTester))
                .toList();
        setContent(content);
        bindBackButton(ctx, "main");
    }

    private GuiItem toItem(GuiContext ctx, Player target, PlayerToolsManager playerTools,
                            PlayerActionService actionService, PermissionDebugManager permissionDebugManager,
                            PlaceholderTester placeholderTester) {
        var builder = new ItemBuilder(Material.PLAYER_HEAD)
                .name(MessageUtil.render("<white>{name}", Map.of("name", target.getName())))
                .lore(MessageUtil.renderLines(List.of(
                        "<gray>World: <white>{world}",
                        "<gray>Click for details"
                ), Map.of("world", target.getWorld().getName())));

        var item = builder.build();
        ItemMetaUtil.edit(item, meta -> {
            if (meta instanceof SkullMeta skullMeta) {
                skullMeta.setOwningPlayer(target);
            }
        });

        return new GuiItem(item, (player, click) -> ctx.guiManager().openMenu(player,
                new PlayerDetailMenu(ctx, target, actionService, permissionDebugManager, placeholderTester)));
    }
}
