package dev.aponder.astracontrol.commands.subcommands;

import dev.aponder.astracontrol.broadcast.BroadcastManager;
import dev.aponder.astracontrol.commands.BaseCommand;
import dev.aponder.astracontrol.commands.CommandContext;
import dev.aponder.astracontrol.config.ConfigManager;
import dev.aponder.astracontrol.language.LanguageManager;
import dev.aponder.astracontrol.language.TranslationKey;
import dev.aponder.astracontrol.maintenance.MaintenanceManager;

import java.util.List;
import java.util.Map;

public final class MaintenanceCommand extends BaseCommand {

    private final MaintenanceManager maintenanceManager;
    private final BroadcastManager broadcastManager;
    private final ConfigManager config;

    public MaintenanceCommand(LanguageManager language, MaintenanceManager maintenanceManager,
                               BroadcastManager broadcastManager, ConfigManager config) {
        super(language);
        this.maintenanceManager = maintenanceManager;
        this.broadcastManager = broadcastManager;
        this.config = config;
    }

    @Override
    public String name() {
        return "maintenance";
    }

    @Override
    public String permission() {
        return "astracontrol.maintenance";
    }

    @Override
    public String usage() {
        return "/astractrl maintenance <on|off|status>";
    }

    @Override
    public String description() {
        return "Enables, disables, or checks maintenance mode.";
    }

    @Override
    public List<String> tabComplete(CommandContext ctx) {
        if (ctx.argCount() <= 1) {
            return List.of("on", "off", "status");
        }
        return List.of();
    }

    @Override
    public void execute(CommandContext ctx) {
        String sub = ctx.arg(0);
        if (sub == null) {
            invalidUsage(ctx);
            return;
        }

        switch (sub.toLowerCase()) {
            case "on" -> {
                if (maintenanceManager.isEnabled()) {
                    language.send(ctx.sender(), TranslationKey.MAINTENANCE_ALREADY_ENABLED);
                    return;
                }
                maintenanceManager.enable(ctx.joinArgsFrom(1).isBlank() ? null : ctx.joinArgsFrom(1));
                language.send(ctx.sender(), TranslationKey.MAINTENANCE_ENABLED);
                announce(true);
            }
            case "off" -> {
                if (!maintenanceManager.isEnabled()) {
                    language.send(ctx.sender(), TranslationKey.MAINTENANCE_ALREADY_DISABLED);
                    return;
                }
                maintenanceManager.disable();
                language.send(ctx.sender(), TranslationKey.MAINTENANCE_DISABLED);
                announce(false);
            }
            case "status" -> language.send(ctx.sender(), TranslationKey.MAINTENANCE_STATUS,
                    Map.of("state", maintenanceManager.isEnabled() ? "Enabled" : "Disabled"));
            default -> invalidUsage(ctx);
        }
    }

    private void announce(boolean enabled) {
        if (!config.isMaintenanceBroadcastOnToggle()) {
            return;
        }
        broadcastManager.broadcastChat(enabled
                ? "<gold>Maintenance mode is now <green>enabled</green>."
                : "<gold>Maintenance mode is now <red>disabled</red>.");
    }
}
