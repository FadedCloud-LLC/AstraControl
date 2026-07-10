package dev.aponder.astracontrol.commands.subcommands;

import dev.aponder.astracontrol.commands.BaseCommand;
import dev.aponder.astracontrol.commands.CommandContext;
import dev.aponder.astracontrol.config.ConfigManager;
import dev.aponder.astracontrol.health.PlatformDetector;
import dev.aponder.astracontrol.language.LanguageManager;
import dev.aponder.astracontrol.logging.HookStatusRegistry;
import dev.aponder.astracontrol.maintenance.MaintenanceManager;
import dev.aponder.astracontrol.text.MessageUtil;

import java.util.Map;

public final class StatusCommand extends BaseCommand {

    private final ConfigManager config;
    private final HookStatusRegistry hookStatusRegistry;
    private final MaintenanceManager maintenanceManager;
    private final String version;

    public StatusCommand(LanguageManager language, ConfigManager config, HookStatusRegistry hookStatusRegistry,
                          MaintenanceManager maintenanceManager, String version) {
        super(language);
        this.config = config;
        this.hookStatusRegistry = hookStatusRegistry;
        this.maintenanceManager = maintenanceManager;
        this.version = version;
    }

    @Override
    public String name() {
        return "status";
    }

    @Override
    public String permission() {
        return "astracontrol.status";
    }

    @Override
    public String usage() {
        return "/astractrl status";
    }

    @Override
    public String description() {
        return "Shows AstraControl's overall status.";
    }

    @Override
    public void execute(CommandContext ctx) {
        long activeHooks = hookStatusRegistry.activeHooks().size();
        long totalHooks = hookStatusRegistry.all().size();

        MessageUtil.send(ctx.sender(), MessageUtil.render(
                "<gray>[<gold>AstraControl</gold><gray>] <white>v{version}\n"
                        + "<gray>Platform: <white>{platform}\n"
                        + "<gray>Hooks: <white>{active}/{total} active\n"
                        + "<gray>Maintenance: <white>{maintenance}\n"
                        + "<gray>Metrics: <white>{metrics}",
                Map.of(
                        "version", version,
                        "platform", PlatformDetector.displayName(),
                        "active", String.valueOf(activeHooks),
                        "total", String.valueOf(totalHooks),
                        "maintenance", maintenanceManager.isEnabled() ? "Enabled" : "Disabled",
                        "metrics", config.isMetricsEnabled() ? "Enabled" : "Disabled"
                )));
    }
}
