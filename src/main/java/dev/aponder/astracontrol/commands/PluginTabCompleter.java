package dev.aponder.astracontrol.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Bridges Bukkit's {@link TabCompleter} contract to {@link CommandManager}.
 */
public final class PluginTabCompleter implements TabCompleter {

    private final CommandManager commandManager;

    public PluginTabCompleter(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                       @NotNull String alias, String[] args) {
        return commandManager.tabComplete(sender, args);
    }
}
