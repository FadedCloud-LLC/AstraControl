package dev.aponder.astracontrol.gui.menus;

import dev.aponder.astracontrol.gui.BaseMenu;
import dev.aponder.astracontrol.gui.GuiContext;
import dev.aponder.astracontrol.gui.GuiItem;
import dev.aponder.astracontrol.gui.GuiLayout;
import dev.aponder.astracontrol.items.ConfigItemBuilder;
import dev.aponder.astracontrol.items.ItemBuilder;
import dev.aponder.astracontrol.items.ItemMetaUtil;
import dev.aponder.astracontrol.plugins.PluginDependencyAnalyzer;
import dev.aponder.astracontrol.plugins.PluginInfo;
import dev.aponder.astracontrol.plugins.PluginReloadRegistry;
import dev.aponder.astracontrol.text.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * Read-only detail view for a single plugin, plus an optional "run reload command"
 * action if the server owner has configured one for this plugin in {@code hooks.yml}.
 */
public final class PluginDetailMenu extends BaseMenu {

    public PluginDetailMenu(GuiContext ctx, PluginInfo info, PluginReloadRegistry reloadRegistry) {
        super(GuiLayout.parse(ctx.guiConfig().menuSection("plugin-detail")));
        ConfigurationSection section = ctx.guiConfig().menuSection("plugin-detail");

        PluginDependencyAnalyzer analyzer = new PluginDependencyAnalyzer();
        List<PluginDependencyAnalyzer.DependencyStatus> statuses = analyzer.analyze(info);

        bindDependencyItem(section, "dependencies",
                statuses.stream().filter(PluginDependencyAnalyzer.DependencyStatus::required).toList());
        bindDependencyItem(section, "soft-dependencies",
                statuses.stream().filter(s -> !s.required()).toList());

        ConfigurationSection reloadItemSection = section != null ? section.getConfigurationSection("items.reload") : null;
        if (reloadItemSection != null) {
            int slot = reloadItemSection.getInt("slot");
            var item = new ItemBuilder(Material.SUNFLOWER)
                    .name(MessageUtil.render("<green>Run Reload Command",
                            Map.of()))
                    .lore(MessageUtil.renderLines(List.of(
                            "<gray>Runs the reload command configured",
                            "<gray>for <white>" + info.name() + "</white> in hooks.yml."
                    ), Map.of()))
                    .build();
            setItem(slot, new GuiItem(item, (player, click) ->
                    reloadRegistry.commandFor(info.name())
                            .ifPresent(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command))));
        }

        bindBackButton(ctx, "plugin-list");
    }

    private void bindDependencyItem(ConfigurationSection section, String itemKey,
                                     List<PluginDependencyAnalyzer.DependencyStatus> statuses) {
        if (section == null) {
            return;
        }
        ConfigurationSection itemSection = section.getConfigurationSection("items." + itemKey);
        if (itemSection == null) {
            return;
        }
        int slot = itemSection.getInt("slot");
        ItemStack item = ConfigItemBuilder.build(itemSection, Map.of());
        List<Component> lore = statuses.isEmpty()
                ? List.of(MessageUtil.render("<gray>None", Map.of()))
                : MessageUtil.renderLines(statuses.stream().map(this::formatDependencyLine).toList(), Map.of());
        ItemMetaUtil.setLore(item, lore);
        setItem(slot, new GuiItem(item));
    }

    private String formatDependencyLine(PluginDependencyAnalyzer.DependencyStatus status) {
        String state = status.enabled() ? " <green>✔" : (status.present() ? " <yellow>?" : " <red>✘");
        return "<gray>" + status.name() + state;
    }
}
