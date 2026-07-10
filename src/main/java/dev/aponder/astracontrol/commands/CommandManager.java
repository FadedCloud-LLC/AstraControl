package dev.aponder.astracontrol.commands;

import dev.aponder.astracontrol.language.LanguageManager;
import dev.aponder.astracontrol.language.TranslationKey;
import dev.aponder.astracontrol.logging.PluginLogger;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registers and dispatches every {@code /astractrl} subcommand. Also the single
 * source of tab-completion for the root command.
 */
public final class CommandManager {

    private final Map<String, SubCommand> commandsByName = new LinkedHashMap<>();
    private final Map<String, SubCommand> commandsByAlias = new LinkedHashMap<>();
    private final LanguageManager language;
    private final CommandCooldownManager cooldownManager;
    private final PluginLogger logger;

    public CommandManager(LanguageManager language, CommandCooldownManager cooldownManager, PluginLogger logger) {
        this.language = language;
        this.cooldownManager = cooldownManager;
        this.logger = logger;
    }

    public void register(SubCommand command) {
        commandsByName.put(command.name().toLowerCase(), command);
        for (String alias : command.aliases()) {
            commandsByAlias.put(alias.toLowerCase(), command);
        }
    }

    public void clear() {
        commandsByName.clear();
        commandsByAlias.clear();
    }

    public List<SubCommand> registeredCommands() {
        return List.copyOf(commandsByName.values());
    }

    public void dispatch(CommandSender sender, String label, String[] rawArgs) {
        if (!sender.hasPermission("astracontrol.use")) {
            language.send(sender, TranslationKey.NO_PERMISSION);
            return;
        }

        String[] effectiveArgs = rawArgs;
        if (rawArgs.length == 0) {
            effectiveArgs = new String[] {sender instanceof org.bukkit.entity.Player ? "gui" : "help"};
        }

        SubCommand command = resolve(effectiveArgs[0]);
        if (command == null) {
            language.send(sender, TranslationKey.UNKNOWN_COMMAND);
            return;
        }

        if (command.permission() != null && !sender.hasPermission(command.permission())) {
            language.send(sender, TranslationKey.NO_PERMISSION);
            return;
        }

        if (sender instanceof org.bukkit.entity.Player player && command.cooldownSeconds() > 0) {
            long cooldownMillis = command.cooldownSeconds() * 1000L;
            if (cooldownManager.isOnCooldown(player.getUniqueId(), command.name(), cooldownMillis)) {
                long remainingSeconds = cooldownManager.remainingMillis(player.getUniqueId(), command.name(), cooldownMillis) / 1000 + 1;
                language.send(sender, TranslationKey.COMMAND_ON_COOLDOWN,
                        Map.of("seconds", String.valueOf(remainingSeconds)));
                return;
            }
            cooldownManager.markUsed(player.getUniqueId(), command.name());
        }

        String[] remaining = Arrays.copyOfRange(effectiveArgs, 1, effectiveArgs.length);
        try {
            command.execute(new CommandContext(sender, label, remaining));
        } catch (Exception exception) {
            logger.error("Unhandled exception while executing '/" + label + " " + command.name() + "'", exception);
            language.send(sender, TranslationKey.COMMAND_ERROR);
        }
    }

    public List<String> tabComplete(CommandSender sender, String[] rawArgs) {
        if (rawArgs.length <= 1) {
            String prefix = rawArgs.length == 1 ? rawArgs[0].toLowerCase() : "";
            List<String> names = new ArrayList<>(commandsByName.keySet());
            return names.stream()
                    .filter(n -> n.startsWith(prefix))
                    .filter(n -> isPermitted(sender, commandsByName.get(n)))
                    .sorted()
                    .toList();
        }

        SubCommand command = resolve(rawArgs[0]);
        if (command == null || !isPermitted(sender, command)) {
            return List.of();
        }
        String[] remaining = Arrays.copyOfRange(rawArgs, 1, rawArgs.length);
        return command.tabComplete(new CommandContext(sender, "", remaining));
    }

    private boolean isPermitted(CommandSender sender, SubCommand command) {
        return command.permission() == null || sender.hasPermission(command.permission());
    }

    private SubCommand resolve(String name) {
        String key = name.toLowerCase();
        SubCommand command = commandsByName.get(key);
        return command != null ? command : commandsByAlias.get(key);
    }
}
