package dev.aponder.astracontrol.commands.subcommands;

import dev.aponder.astracontrol.commands.BaseCommand;
import dev.aponder.astracontrol.commands.CommandContext;
import dev.aponder.astracontrol.language.LanguageManager;
import dev.aponder.astracontrol.language.TranslationKey;
import dev.aponder.astracontrol.lifecycle.ReloadManager;
import dev.aponder.astracontrol.lifecycle.ReloadResult;

import java.util.Map;

public final class ReloadCommand extends BaseCommand {

    private final ReloadManager reloadManager;

    public ReloadCommand(LanguageManager language, ReloadManager reloadManager) {
        super(language);
        this.reloadManager = reloadManager;
    }

    @Override
    public String name() {
        return "reload";
    }

    @Override
    public String permission() {
        return "astracontrol.reload";
    }

    @Override
    public String usage() {
        return "/astractrl reload";
    }

    @Override
    public String description() {
        return "Reloads AstraControl's configuration, language, and hooks.";
    }

    @Override
    public int cooldownSeconds() {
        return 3;
    }

    @Override
    public void execute(CommandContext ctx) {
        language.send(ctx.sender(), TranslationKey.RELOAD_STARTED);
        ReloadResult result = reloadManager.reload();
        if (result.success()) {
            language.send(ctx.sender(), TranslationKey.RELOAD_SUCCESS, Map.of("time", String.valueOf(result.elapsedMillis())));
        } else {
            language.send(ctx.sender(), TranslationKey.RELOAD_FAILED, Map.of("error", String.valueOf(result.errorMessage())));
        }
    }
}
