package dev.aponder.astracontrol.gui.menus;

import dev.aponder.astracontrol.gui.GuiContext;
import dev.aponder.astracontrol.gui.GuiItem;
import dev.aponder.astracontrol.gui.GuiLayout;
import dev.aponder.astracontrol.gui.PaginatedMenu;
import dev.aponder.astracontrol.hooks.HookStatus;
import dev.aponder.astracontrol.items.ItemBuilder;
import dev.aponder.astracontrol.logging.HookStatusRegistry;
import dev.aponder.astracontrol.text.MessageUtil;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

/**
 * Shows the detection status of every optional integration.
 */
public final class HookStatusMenu extends PaginatedMenu {

    public HookStatusMenu(GuiContext ctx, HookStatusRegistry registry) {
        super(GuiLayout.parse(ctx.guiConfig().menuSection("hooks")));

        List<GuiItem> content = registry.all().stream()
                .sorted((a, b) -> a.displayName().compareToIgnoreCase(b.displayName()))
                .map(this::toItem)
                .toList();
        setContent(content);
        bindBackButton(ctx, "main");
    }

    private GuiItem toItem(HookStatus status) {
        Material material = status.enabled() ? Material.LIME_DYE : (status.installed() ? Material.YELLOW_DYE : Material.GRAY_DYE);
        String stateLabel = status.enabled() ? "<green>Active" : (status.installed() ? "<yellow>Installed (disabled)" : "<red>Not installed");

        var item = new ItemBuilder(material)
                .name(MessageUtil.render("<white>{name}", Map.of("name", status.displayName())))
                .lore(MessageUtil.renderLines(List.of(
                        "<gray>Status: " + stateLabel,
                        "<gray>Version: <white>{version}",
                        "<gray>{notes}"
                ), Map.of("version", status.version(), "notes", status.notes())))
                .build();
        return new GuiItem(item);
    }
}
