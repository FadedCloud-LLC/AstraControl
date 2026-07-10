package dev.aponder.astracontrol.commands.subcommands;

import dev.aponder.astracontrol.commands.BaseCommand;
import dev.aponder.astracontrol.commands.CommandContext;
import dev.aponder.astracontrol.hooks.HookStatus;
import dev.aponder.astracontrol.language.LanguageManager;
import dev.aponder.astracontrol.logging.HookStatusRegistry;
import dev.aponder.astracontrol.text.MessageUtil;

import java.util.Map;
import java.util.stream.Collectors;

public final class HooksCommand extends BaseCommand {

    private final HookStatusRegistry registry;

    public HooksCommand(LanguageManager language, HookStatusRegistry registry) {
        super(language);
        this.registry = registry;
    }

    @Override
    public String name() {
        return "hooks";
    }

    @Override
    public String permission() {
        return "astracontrol.hooks";
    }

    @Override
    public String usage() {
        return "/astractrl hooks";
    }

    @Override
    public String description() {
        return "Shows the status of every optional integration.";
    }

    @Override
    public void execute(CommandContext ctx) {
        String list = registry.all().stream()
                .sorted((a, b) -> a.displayName().compareToIgnoreCase(b.displayName()))
                .map(this::formatHook)
                .collect(Collectors.joining("\n"));

        MessageUtil.send(ctx.sender(), MessageUtil.render(
                "<gray>[<gold>AstraControl</gold><gray>] <white>Hook Status:\n{list}",
                Map.of("list", list.isBlank() ? "None checked" : list)));
    }

    private String formatHook(HookStatus status) {
        String color = status.enabled() ? "<green>" : (status.installed() ? "<yellow>" : "<red>");
        String state = status.enabled() ? "Active" : (status.installed() ? "Installed (disabled)" : "Not installed");
        return color + status.displayName() + " <gray>- " + state;
    }
}
