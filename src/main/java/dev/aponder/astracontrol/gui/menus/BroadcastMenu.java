package dev.aponder.astracontrol.gui.menus;

import dev.aponder.astracontrol.broadcast.BroadcastManager;
import dev.aponder.astracontrol.broadcast.BroadcastRequest;
import dev.aponder.astracontrol.gui.BaseMenu;
import dev.aponder.astracontrol.gui.GuiContext;
import dev.aponder.astracontrol.gui.GuiLayout;
import dev.aponder.astracontrol.gui.MenuSession;
import dev.aponder.astracontrol.language.TranslationKey;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

/**
 * Sends chat, title, actionbar, and bossbar broadcasts. Every prompt is a simple
 * chat-input capture so this menu never needs its own text-entry sign/anvil GUI.
 */
public final class BroadcastMenu extends BaseMenu {

    public BroadcastMenu(GuiContext ctx, BroadcastManager broadcastManager) {
        super(GuiLayout.parse(ctx.guiConfig().menuSection("broadcast")));
        ConfigurationSection section = ctx.guiConfig().menuSection("broadcast");

        bindConfigButton(section, "chat", "astracontrol.broadcast", ctx.language(), (player, click) -> {
            player.closeInventory();
            prompt(ctx, player, "Type the chat message to broadcast", message ->
                    complete(ctx, player, broadcastManager.execute(BroadcastRequest.chat(message))));
        });

        bindConfigButton(section, "title", "astracontrol.broadcast", ctx.language(), (player, click) -> {
            player.closeInventory();
            prompt(ctx, player, "Type the title (use '|' to add a subtitle)", input -> {
                String[] parts = input.split("\\|", 2);
                String title = parts[0];
                String subtitle = parts.length > 1 ? parts[1] : "";
                complete(ctx, player, broadcastManager.execute(BroadcastRequest.title(title, subtitle)));
            });
        });

        bindConfigButton(section, "actionbar", "astracontrol.broadcast", ctx.language(), (player, click) -> {
            player.closeInventory();
            prompt(ctx, player, "Type the actionbar message to broadcast", message ->
                    complete(ctx, player, broadcastManager.execute(BroadcastRequest.actionbar(message))));
        });

        bindConfigButton(section, "bossbar", "astracontrol.broadcast", ctx.language(), (player, click) -> {
            player.closeInventory();
            prompt(ctx, player, "Type the bossbar message to broadcast", message ->
                    complete(ctx, player, broadcastManager.execute(BroadcastRequest.bossbar(message))));
        });

        bindBackButton(ctx, "main");
    }

    private void prompt(GuiContext ctx, org.bukkit.entity.Player player, String description, java.util.function.Consumer<String> onInput) {
        MenuSession session = ctx.guiManager().sessions().getOrCreate(player.getUniqueId());
        session.awaitChatInput(description, onInput, null);
    }

    private void complete(GuiContext ctx, org.bukkit.entity.Player player, int count) {
        ctx.language().send(player, TranslationKey.BROADCAST_SENT, Map.of("count", String.valueOf(count)));
    }
}
