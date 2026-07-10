package dev.aponder.astracontrol.gui.menus;

import dev.aponder.astracontrol.errors.ErrorBuffer;
import dev.aponder.astracontrol.errors.ErrorExporter;
import dev.aponder.astracontrol.errors.ErrorRecord;
import dev.aponder.astracontrol.gui.GuiContext;
import dev.aponder.astracontrol.gui.GuiItem;
import dev.aponder.astracontrol.gui.GuiLayout;
import dev.aponder.astracontrol.gui.PaginatedMenu;
import dev.aponder.astracontrol.items.ItemBuilder;
import dev.aponder.astracontrol.language.TranslationKey;
import dev.aponder.astracontrol.scheduler.SchedulerAdapter;
import dev.aponder.astracontrol.text.MessageUtil;
import dev.aponder.astracontrol.text.TextFormatter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Shows recently captured warnings/errors, with clear and export actions.
 */
public final class ErrorWatcherMenu extends PaginatedMenu {

    public ErrorWatcherMenu(GuiContext ctx, ErrorBuffer buffer, ErrorExporter exporter,
                             JavaPlugin plugin, SchedulerAdapter scheduler) {
        super(GuiLayout.parse(ctx.guiConfig().menuSection("errors")));
        ConfigurationSection section = ctx.guiConfig().menuSection("errors");

        List<GuiItem> content = buffer.snapshot().stream().map(this::toItem).toList();
        setContent(content);

        if (section != null && section.isInt("clear-slot")) {
            int slot = section.getInt("clear-slot");
            var item = new ItemBuilder(Material.BARRIER).name(MessageUtil.render("<red>Clear Errors", Map.of())).build();
            setItem(slot, new GuiItem(item, (player, click) -> {
                if (!player.hasPermission("astracontrol.errors.clear")) {
                    ctx.language().send(player, TranslationKey.NO_PERMISSION);
                    return;
                }
                int count = buffer.size();
                buffer.clear();
                ctx.language().send(player, TranslationKey.ERRORS_CLEARED, Map.of("count", String.valueOf(count)));
                ctx.guiManager().openMenu(player, new ErrorWatcherMenu(ctx, buffer, exporter, plugin, scheduler));
            }));
        }

        if (section != null && section.isInt("export-slot")) {
            int slot = section.getInt("export-slot");
            var item = new ItemBuilder(Material.CHEST).name(MessageUtil.render("<green>Export Errors", Map.of())).build();
            setItem(slot, new GuiItem(item, (player, click) -> {
                if (!player.hasPermission("astracontrol.errors.export")) {
                    ctx.language().send(player, TranslationKey.NO_PERMISSION);
                    return;
                }
                player.closeInventory();
                int count = buffer.size();
                List<ErrorRecord> snapshot = buffer.snapshot();
                scheduler.runAsync(() -> {
                    try {
                        File file = exporter.export(new File(plugin.getDataFolder(), "logs"), snapshot);
                        scheduler.runNow(() -> ctx.language().send(player, TranslationKey.ERRORS_EXPORTED,
                                Map.of("count", String.valueOf(count), "file", file.getName())));
                    } catch (IOException e) {
                        scheduler.runNow(() -> ctx.language().send(player, TranslationKey.RELOAD_FAILED,
                                Map.of("error", String.valueOf(e.getMessage()))));
                    }
                });
            }));
        }

        bindBackButton(ctx, "main");
    }

    private GuiItem toItem(ErrorRecord record) {
        Material material = "ERROR".equalsIgnoreCase(record.level()) ? Material.REDSTONE_BLOCK : Material.REDSTONE;
        var item = new ItemBuilder(material)
                .name(MessageUtil.render("<white>{source} <gray>x{count}",
                        Map.of("source", record.source(), "count", String.valueOf(record.count()))))
                .lore(MessageUtil.renderLines(List.of(
                        "<gray>{level}: <white>{message}",
                        "<gray>Last seen: <white>{time}"
                ), Map.of(
                        "level", record.level(),
                        "message", TextFormatter.truncate(record.message(), 200),
                        "time", Instant.ofEpochMilli(record.lastSeenMillis()).toString()
                )))
                .build();
        return new GuiItem(item);
    }
}
