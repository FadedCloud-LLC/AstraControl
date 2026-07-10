package dev.aponder.astracontrol.commands.subcommands;

import dev.aponder.astracontrol.broadcast.BroadcastManager;
import dev.aponder.astracontrol.broadcast.BroadcastRequest;
import dev.aponder.astracontrol.commands.BaseCommand;
import dev.aponder.astracontrol.commands.CommandContext;
import dev.aponder.astracontrol.language.LanguageManager;
import dev.aponder.astracontrol.language.TranslationKey;

import java.util.Map;

/**
 * Backs {@code /astractrl broadcast}, {@code title}, and {@code actionbar} - one
 * instance per top-level command name, each configured with its {@link Mode}.
 */
public final class BroadcastCommand extends BaseCommand {

    public enum Mode {
        CHAT, TITLE, ACTIONBAR
    }

    private final BroadcastManager broadcastManager;
    private final Mode mode;
    private final String commandName;

    public BroadcastCommand(LanguageManager language, BroadcastManager broadcastManager, Mode mode, String commandName) {
        super(language);
        this.broadcastManager = broadcastManager;
        this.mode = mode;
        this.commandName = commandName;
    }

    @Override
    public String name() {
        return commandName;
    }

    @Override
    public String permission() {
        return "astracontrol.broadcast";
    }

    @Override
    public String usage() {
        return "/astractrl " + commandName + " <message>";
    }

    @Override
    public String description() {
        return switch (mode) {
            case CHAT -> "Broadcasts a chat message to every online player.";
            case TITLE -> "Broadcasts a title (use '|' for a subtitle).";
            case ACTIONBAR -> "Broadcasts an actionbar message.";
        };
    }

    @Override
    public int cooldownSeconds() {
        return 2;
    }

    @Override
    public void execute(CommandContext ctx) {
        if (ctx.argCount() < 1) {
            invalidUsage(ctx);
            return;
        }
        String input = ctx.joinArgsFrom(0);
        int count = switch (mode) {
            case CHAT -> broadcastManager.execute(BroadcastRequest.chat(input));
            case TITLE -> {
                String[] parts = input.split("\\|", 2);
                yield broadcastManager.execute(BroadcastRequest.title(parts[0], parts.length > 1 ? parts[1] : ""));
            }
            case ACTIONBAR -> broadcastManager.execute(BroadcastRequest.actionbar(input));
        };
        language.send(ctx.sender(), TranslationKey.BROADCAST_SENT, Map.of("count", String.valueOf(count)));
    }
}
