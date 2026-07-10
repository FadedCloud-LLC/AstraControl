package dev.aponder.astracontrol.gui.menus;

import dev.aponder.astracontrol.gui.BaseMenu;
import dev.aponder.astracontrol.gui.GuiContext;
import dev.aponder.astracontrol.gui.GuiLayout;
import dev.aponder.astracontrol.health.ServerHealthManager;
import dev.aponder.astracontrol.health.ServerHealthSnapshot;
import dev.aponder.astracontrol.text.TextFormatter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

/**
 * Read-only server health dashboard.
 */
public final class HealthMenu extends BaseMenu {

    public HealthMenu(GuiContext ctx, ServerHealthManager healthManager) {
        super(GuiLayout.parse(ctx.guiConfig().menuSection("health")));
        ConfigurationSection section = ctx.guiConfig().menuSection("health");
        ServerHealthSnapshot snapshot = healthManager.currentSnapshot();

        bindDecoration(section, "tps", Map.of("value",
                snapshot.tpsAvailable() ? TextFormatter.decimal(snapshot.currentTps(), 2) : "Unavailable"));
        bindDecoration(section, "mspt", Map.of("value",
                snapshot.msptAvailable() ? TextFormatter.decimal(snapshot.mspt(), 2) + "ms" : "Unavailable"));
        bindDecoration(section, "memory", Map.of(
                "used", TextFormatter.bytesToMegabytes(snapshot.usedMemoryBytes()),
                "max", TextFormatter.bytesToMegabytes(snapshot.maxMemoryBytes()),
                "percent", TextFormatter.percent(snapshot.maxMemoryBytes() > 0
                        ? (snapshot.usedMemoryBytes() * 100.0) / snapshot.maxMemoryBytes() : 0.0)));
        bindDecoration(section, "uptime", Map.of("value", TextFormatter.duration(snapshot.uptime())));
        bindDecoration(section, "players", Map.of("value", String.valueOf(snapshot.onlinePlayers())));
        bindDecoration(section, "worlds", Map.of("value", String.valueOf(snapshot.loadedWorlds())));
        bindDecoration(section, "chunks", Map.of("value", String.valueOf(snapshot.loadedChunks())));
        bindDecoration(section, "entities", Map.of(
                "value", snapshot.entityCount() >= 0 ? String.valueOf(snapshot.entityCount()) : "Unavailable",
                "tile-entities", snapshot.tileEntityCount() >= 0 ? String.valueOf(snapshot.tileEntityCount()) : "Unavailable"));
        bindDecoration(section, "platform", Map.of(
                "value", snapshot.platformName(),
                "folia", snapshot.folia() ? "Yes" : "No"));
        bindDecoration(section, "version", Map.of(
                "server", snapshot.serverVersion(),
                "java", snapshot.javaVersion()));

        bindBackButton(ctx, "main");
    }
}
