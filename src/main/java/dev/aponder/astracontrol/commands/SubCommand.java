package dev.aponder.astracontrol.commands;

import java.util.List;

/**
 * A single {@code /astractrl <name> ...} subcommand. Implementations are responsible
 * for parsing/dispatching their own nested arguments (e.g. {@code errors clear}).
 */
public interface SubCommand {

    String name();

    default List<String> aliases() {
        return List.of();
    }

    /**
     * Required permission, or {@code null} if only the base
     * {@code astracontrol.use} permission is required.
     */
    String permission();

    String usage();

    String description();

    void execute(CommandContext context);

    default List<String> tabComplete(CommandContext context) {
        return List.of();
    }

    /**
     * Per-player cooldown in seconds. {@code 0} (the default) disables cooldown
     * enforcement for this command. Never applied to console/command-block senders.
     */
    default int cooldownSeconds() {
        return 0;
    }
}
