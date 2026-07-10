package dev.aponder.astracontrol.commands.subcommands;

import dev.aponder.astracontrol.commands.BaseCommand;
import dev.aponder.astracontrol.commands.CommandContext;
import dev.aponder.astracontrol.errors.ErrorBuffer;
import dev.aponder.astracontrol.errors.ErrorExporter;
import dev.aponder.astracontrol.errors.ErrorRecord;
import dev.aponder.astracontrol.language.LanguageManager;
import dev.aponder.astracontrol.language.TranslationKey;
import dev.aponder.astracontrol.scheduler.SchedulerAdapter;
import dev.aponder.astracontrol.text.MessageUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ErrorsCommand extends BaseCommand {

    private final ErrorBuffer buffer;
    private final ErrorExporter exporter;
    private final JavaPlugin plugin;
    private final SchedulerAdapter scheduler;

    public ErrorsCommand(LanguageManager language, ErrorBuffer buffer, ErrorExporter exporter,
                          JavaPlugin plugin, SchedulerAdapter scheduler) {
        super(language);
        this.buffer = buffer;
        this.exporter = exporter;
        this.plugin = plugin;
        this.scheduler = scheduler;
    }

    @Override
    public String name() {
        return "errors";
    }

    @Override
    public String permission() {
        return "astracontrol.errors";
    }

    @Override
    public String usage() {
        return "/astractrl errors [clear|export]";
    }

    @Override
    public String description() {
        return "Shows, clears, or exports captured warnings/errors.";
    }

    @Override
    public List<String> tabComplete(CommandContext ctx) {
        if (ctx.argCount() <= 1) {
            return List.of("clear", "export");
        }
        return List.of();
    }

    @Override
    public void execute(CommandContext ctx) {
        String sub = ctx.arg(0);
        if (sub == null) {
            showErrors(ctx);
        } else if (sub.equalsIgnoreCase("clear")) {
            clearErrors(ctx);
        } else if (sub.equalsIgnoreCase("export")) {
            exportErrors(ctx);
        } else {
            invalidUsage(ctx);
        }
    }

    private void showErrors(CommandContext ctx) {
        List<ErrorRecord> records = buffer.snapshot();
        if (records.isEmpty()) {
            language.send(ctx.sender(), TranslationKey.ERRORS_EMPTY);
            return;
        }
        String list = records.stream()
                .map(r -> "<white>" + r.source() + " <gray>(" + r.level() + ", x" + r.count() + "): " + r.message())
                .collect(Collectors.joining("\n"));
        MessageUtil.send(ctx.sender(), MessageUtil.render(
                "<gray>[<gold>AstraControl</gold><gray>] <white>{count} recorded error(s):\n{list}",
                Map.of("count", String.valueOf(records.size()), "list", list)));
    }

    private void clearErrors(CommandContext ctx) {
        if (!ctx.sender().hasPermission("astracontrol.errors.clear")) {
            language.send(ctx.sender(), TranslationKey.NO_PERMISSION);
            return;
        }
        int count = buffer.size();
        buffer.clear();
        language.send(ctx.sender(), TranslationKey.ERRORS_CLEARED, Map.of("count", String.valueOf(count)));
    }

    private void exportErrors(CommandContext ctx) {
        if (!ctx.sender().hasPermission("astracontrol.errors.export")) {
            language.send(ctx.sender(), TranslationKey.NO_PERMISSION);
            return;
        }
        int count = buffer.size();
        List<ErrorRecord> snapshot = buffer.snapshot();
        scheduler.runAsync(() -> {
            try {
                File file = exporter.export(new File(plugin.getDataFolder(), "logs"), snapshot);
                scheduler.runNow(() -> language.send(ctx.sender(), TranslationKey.ERRORS_EXPORTED,
                        Map.of("count", String.valueOf(count), "file", file.getName())));
            } catch (IOException e) {
                scheduler.runNow(() -> language.send(ctx.sender(), TranslationKey.RELOAD_FAILED,
                        Map.of("error", String.valueOf(e.getMessage()))));
            }
        });
    }
}
