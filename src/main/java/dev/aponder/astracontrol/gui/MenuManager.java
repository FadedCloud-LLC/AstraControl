package dev.aponder.astracontrol.gui;

import dev.aponder.astracontrol.scheduler.SchedulerAdapter;
import dev.aponder.astracontrol.text.ColorUtil;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;

/**
 * Single listener for every AstraControl menu. Every click/drag touching an
 * AstraControl inventory is cancelled unconditionally - items are never movable, so
 * there is no duplication surface.
 */
public final class MenuManager implements Listener {

    private final MenuSessionManager sessionManager;
    private final SchedulerAdapter scheduler;

    public MenuManager(MenuSessionManager sessionManager, SchedulerAdapter scheduler) {
        this.sessionManager = sessionManager;
        this.scheduler = scheduler;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onClick(InventoryClickEvent event) {
        InventoryHolder topHolder = event.getView().getTopInventory().getHolder();
        if (!(topHolder instanceof BaseMenu menu)) {
            return;
        }
        event.setCancelled(true);

        if (!(event.getClickedInventory() != null && event.getClickedInventory().getHolder() instanceof BaseMenu)) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        menu.handleClick(player, event.getSlot(), event.getClick());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDrag(InventoryDragEvent event) {
        InventoryHolder topHolder = event.getView().getTopInventory().getHolder();
        if (topHolder instanceof BaseMenu) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof BaseMenu
                && event.getPlayer() instanceof Player player) {
            MenuSession session = sessionManager.get(player.getUniqueId());
            if (session != null) {
                session.clearCurrentMenu();
                cancelPendingChatInput(session);
            }
        }
    }

    private void cancelPendingChatInput(MenuSession session) {
        if (!session.hasPendingChatInput()) {
            return;
        }
        MenuSession.PendingChatInput pending = session.pendingChatInput();
        session.clearPendingChatInput();
        if (pending.onCancel() != null) {
            pending.onCancel().run();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncChatEvent event) {
        MenuSession session = sessionManager.get(event.getPlayer().getUniqueId());
        if (session == null || !session.hasPendingChatInput()) {
            return;
        }
        event.setCancelled(true);
        MenuSession.PendingChatInput pending = session.pendingChatInput();
        session.clearPendingChatInput();
        String message = ColorUtil.toPlain(event.message());
        scheduler.runNow(() -> pending.onInput().accept(message));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        sessionManager.remove(event.getPlayer().getUniqueId());
    }
}
