package dev.aponder.astracontrol.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Bridges Bukkit's {@link CommandExecutor} contract to {@link CommandManager}.
 */
public final class PluginCommandExecutor implements CommandExecutor {

    private final CommandManager commandManager;

    public PluginCommandExecutor(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        commandManager.dispatch(sender, label, args);
        return true;
    }
}
