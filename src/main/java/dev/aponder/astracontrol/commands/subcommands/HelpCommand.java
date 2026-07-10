package dev.aponder.astracontrol.commands.subcommands;

import dev.aponder.astracontrol.commands.BaseCommand;
import dev.aponder.astracontrol.commands.CommandContext;
import dev.aponder.astracontrol.commands.CommandManager;
import dev.aponder.astracontrol.commands.SubCommand;
import dev.aponder.astracontrol.language.LanguageManager;
import dev.aponder.astracontrol.language.TranslationKey;

import java.util.Map;

public final class HelpCommand extends BaseCommand {

    private final CommandManager commandManager;

    public HelpCommand(LanguageManager language, CommandManager commandManager) {
        super(language);
        this.commandManager = commandManager;
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public String permission() {
        return null;
    }

    @Override
    public String usage() {
        return "/astractrl help";
    }

    @Override
    public String description() {
        return "Shows this help menu.";
    }

    @Override
    public void execute(CommandContext ctx) {
        language.send(ctx.sender(), TranslationKey.COMMAND_HELP_HEADER);
        for (SubCommand command : commandManager.registeredCommands()) {
            if (command.permission() != null && !ctx.sender().hasPermission(command.permission())) {
                continue;
            }
            language.send(ctx.sender(), TranslationKey.COMMAND_HELP_ENTRY, Map.of(
                    "command", "astractrl " + command.name(),
                    "description", command.description()
            ));
        }
    }
}
