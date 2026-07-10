package dev.aponder.astracontrol.commands.subcommands;

import dev.aponder.astracontrol.commands.BaseCommand;
import dev.aponder.astracontrol.commands.CommandContext;
import dev.aponder.astracontrol.gui.GUIManager;
import dev.aponder.astracontrol.language.LanguageManager;
import org.bukkit.entity.Player;

public final class GuiCommand extends BaseCommand {

    private final GUIManager guiManager;

    public GuiCommand(LanguageManager language, GUIManager guiManager) {
        super(language);
        this.guiManager = guiManager;
    }

    @Override
    public String name() {
        return "gui";
    }

    @Override
    public String permission() {
        return "astracontrol.gui";
    }

    @Override
    public String usage() {
        return "/astractrl gui";
    }

    @Override
    public String description() {
        return "Opens the AstraControl control center.";
    }

    @Override
    public void execute(CommandContext ctx) {
        Player player = requirePlayer(ctx);
        if (player == null) {
            return;
        }
        guiManager.open(player, "main");
    }
}
