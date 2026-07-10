package dev.aponder.astracontrol.gui;

import org.bukkit.entity.Player;

/**
 * Top-level facade for opening AstraControl menus. Every command/menu button that
 * needs to open a menu should go through this class rather than constructing a menu
 * directly.
 */
public final class GUIManager {

    private final MenuRegistry registry;
    private final MenuSessionManager sessionManager;

    public GUIManager(MenuRegistry registry, MenuSessionManager sessionManager) {
        this.registry = registry;
        this.sessionManager = sessionManager;
    }

    public void open(Player player, String menuKey) {
        registry.create(menuKey, player).ifPresent(menu -> {
            MenuSession session = sessionManager.getOrCreate(player.getUniqueId());
            session.clearPendingChatInput();
            session.setCurrentMenu(menu);
            menu.open(player);
        });
    }

    public void openMenu(Player player, BaseMenu menu) {
        MenuSession session = sessionManager.getOrCreate(player.getUniqueId());
        session.clearPendingChatInput();
        session.setCurrentMenu(menu);
        menu.open(player);
    }

    public MenuRegistry registry() {
        return registry;
    }

    public MenuSessionManager sessions() {
        return sessionManager;
    }
}
