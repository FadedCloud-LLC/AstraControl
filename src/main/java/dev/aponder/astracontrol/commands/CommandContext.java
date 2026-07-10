package dev.aponder.astracontrol.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * The sender, label, and (subcommand-relative) arguments for a single dispatched
 * command invocation.
 */
public final class CommandContext {

    private final CommandSender sender;
    private final String label;
    private final String[] args;

    public CommandContext(CommandSender sender, String label, String[] args) {
        this.sender = sender;
        this.label = label;
        this.args = args;
    }

    public CommandSender sender() {
        return sender;
    }

    public String label() {
        return label;
    }

    public String[] args() {
        return args;
    }

    public int argCount() {
        return args.length;
    }

    public String arg(int index) {
        return index >= 0 && index < args.length ? args[index] : null;
    }

    public String joinArgsFrom(int index) {
        if (index >= args.length) {
            return "";
        }
        return String.join(" ", Arrays.copyOfRange(args, index, args.length));
    }

    public boolean isPlayer() {
        return sender instanceof Player;
    }

    public Player asPlayer() {
        return sender instanceof Player player ? player : null;
    }
}
