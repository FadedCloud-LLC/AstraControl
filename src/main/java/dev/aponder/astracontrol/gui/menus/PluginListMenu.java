package dev.aponder.astracontrol.gui.menus;

import dev.aponder.astracontrol.gui.GuiContext;
import dev.aponder.astracontrol.gui.GuiItem;
import dev.aponder.astracontrol.gui.GuiLayout;
import dev.aponder.astracontrol.gui.PaginatedMenu;
import dev.aponder.astracontrol.items.ItemBuilder;
import dev.aponder.astracontrol.plugins.PluginInfo;
import dev.aponder.astracontrol.plugins.PluginInspector;
import dev.aponder.astracontrol.plugins.PluginReloadRegistry;
import dev.aponder.astracontrol.text.MessageUtil;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

/**
 * Paginated, browsable list of every installed plugin.
 */
public final class PluginListMenu extends PaginatedMenu {

    public PluginListMenu(GuiContext ctx, PluginInspector inspector, PluginReloadRegistry reloadRegistry) {
        super(GuiLayout.parse(ctx.guiConfig().menuSection("plugin-list")));

        List<GuiItem> content = inspector.allPlugins().stream()
                .map(info -> toItem(ctx, info, reloadRegistry))
                .toList();
        setContent(content);
        bindBackButton(ctx, "main");
    }

    private GuiItem toItem(GuiContext ctx, PluginInfo info, PluginReloadRegistry reloadRegistry) {
        Material material = info.enabled() ? Material.LIME_DYE : Material.GRAY_DYE;
        var item = new ItemBuilder(material)
                .name(MessageUtil.render("<white>{name} <gray>v{version}",
                        Map.of("name", info.name(), "version", info.version())))
                .lore(MessageUtil.renderLines(List.of(
                        "<gray>Status: " + (info.enabled() ? "<green>Enabled" : "<red>Disabled"),
                        "<gray>Authors: <white>{authors}",
                        "<gray>Click for details"
                ), Map.of("authors", info.authors().isEmpty() ? "Unknown" : String.join(", ", info.authors()))))
                .build();

        return new GuiItem(item, (player, click) ->
                ctx.guiManager().openMenu(player, new PluginDetailMenu(ctx, info, reloadRegistry)));
    }
}
