package dev.aponder.astracontrol.commands.subcommands;

import dev.aponder.astracontrol.commands.BaseCommand;
import dev.aponder.astracontrol.commands.CommandContext;
import dev.aponder.astracontrol.health.ServerHealthManager;
import dev.aponder.astracontrol.health.ServerHealthSnapshot;
import dev.aponder.astracontrol.language.LanguageManager;
import dev.aponder.astracontrol.text.MessageUtil;
import dev.aponder.astracontrol.text.TextFormatter;

import java.util.Map;

/**
 * Backs {@code /astractrl health}, {@code tps}, {@code memory}, {@code worlds},
 * {@code chunks}, and {@code entities} - one instance per top-level command name,
 * each configured with which {@link Mode} it reports.
 */
public final class HealthCommand extends BaseCommand {

    public enum Mode {
        FULL, TPS, MEMORY, WORLDS, CHUNKS, ENTITIES
    }

    private final ServerHealthManager healthManager;
    private final Mode mode;
    private final String commandName;

    public HealthCommand(LanguageManager language, ServerHealthManager healthManager, Mode mode, String commandName) {
        super(language);
        this.healthManager = healthManager;
        this.mode = mode;
        this.commandName = commandName;
    }

    @Override
    public String name() {
        return commandName;
    }

    @Override
    public String permission() {
        return "astracontrol.health";
    }

    @Override
    public String usage() {
        return "/astractrl " + commandName;
    }

    @Override
    public String description() {
        return switch (mode) {
            case FULL -> "Shows a full server health report.";
            case TPS -> "Shows the current TPS.";
            case MEMORY -> "Shows current memory usage.";
            case WORLDS -> "Shows loaded worlds.";
            case CHUNKS -> "Shows loaded chunk count.";
            case ENTITIES -> "Shows entity and tile-entity counts.";
        };
    }

    @Override
    public void execute(CommandContext ctx) {
        ServerHealthSnapshot snapshot = healthManager.currentSnapshot();
        String template = switch (mode) {
            case FULL -> "<gray>[<gold>AstraControl</gold><gray>] <white>Server Health\n"
                    + "<gray>TPS: <white>{tps} <gray>| MSPT: <white>{mspt}\n"
                    + "<gray>Memory: <white>{used}/{max} ({percent})\n"
                    + "<gray>Uptime: <white>{uptime} <gray>| Players: <white>{players}\n"
                    + "<gray>Worlds: <white>{worlds} <gray>| Chunks: <white>{chunks} <gray>| Entities: <white>{entities}\n"
                    + "<gray>Platform: <white>{platform} <gray>(Folia: {folia})";
            case TPS -> "<gray>TPS: <white>{tps}";
            case MEMORY -> "<gray>Memory: <white>{used}/{max} <gray>({percent})";
            case WORLDS -> "<gray>Loaded worlds: <white>{worlds}";
            case CHUNKS -> "<gray>Loaded chunks: <white>{chunks}";
            case ENTITIES -> "<gray>Entities: <white>{entities} <gray>| Tile entities: <white>{tile-entities}";
        };

        MessageUtil.send(ctx.sender(), MessageUtil.render(template, Map.ofEntries(
                Map.entry("tps", snapshot.tpsAvailable() ? TextFormatter.decimal(snapshot.currentTps(), 2) : "Unavailable"),
                Map.entry("mspt", snapshot.msptAvailable() ? TextFormatter.decimal(snapshot.mspt(), 2) + "ms" : "Unavailable"),
                Map.entry("used", TextFormatter.bytesToMegabytes(snapshot.usedMemoryBytes())),
                Map.entry("max", TextFormatter.bytesToMegabytes(snapshot.maxMemoryBytes())),
                Map.entry("percent", TextFormatter.percent(snapshot.maxMemoryBytes() > 0
                        ? (snapshot.usedMemoryBytes() * 100.0) / snapshot.maxMemoryBytes() : 0.0)),
                Map.entry("uptime", TextFormatter.duration(snapshot.uptime())),
                Map.entry("players", String.valueOf(snapshot.onlinePlayers())),
                Map.entry("worlds", String.valueOf(snapshot.loadedWorlds())),
                Map.entry("chunks", String.valueOf(snapshot.loadedChunks())),
                Map.entry("entities", snapshot.entityCount() >= 0 ? String.valueOf(snapshot.entityCount()) : "Unavailable"),
                Map.entry("tile-entities", snapshot.tileEntityCount() >= 0 ? String.valueOf(snapshot.tileEntityCount()) : "Unavailable"),
                Map.entry("platform", snapshot.platformName()),
                Map.entry("folia", snapshot.folia() ? "Yes" : "No")
        )));
    }
}
