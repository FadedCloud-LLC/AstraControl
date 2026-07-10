package dev.aponder.astracontrol.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * A single clickable (or purely decorative) slot in a menu.
 */
public final class GuiItem {

    private final ItemStack itemStack;
    private final GuiClickHandler clickHandler;

    public GuiItem(ItemStack itemStack) {
        this(itemStack, null);
    }

    public GuiItem(ItemStack itemStack, GuiClickHandler clickHandler) {
        this.itemStack = itemStack;
        this.clickHandler = clickHandler;
    }

    public ItemStack itemStack() {
        return itemStack;
    }

    public void handleClick(Player player, ClickType clickType) {
        if (clickHandler != null) {
            clickHandler.onClick(player, clickType);
        }
    }
}
