package dev.aponder.astracontrol.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

@FunctionalInterface
public interface GuiClickHandler {

    void onClick(Player player, ClickType clickType);
}
