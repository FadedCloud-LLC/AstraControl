package dev.aponder.astracontrol.gui;

import dev.aponder.astracontrol.items.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

/**
 * A generic yes/no confirmation dialog used before destructive or hard-to-reverse
 * actions (clearing errors, toggling maintenance, etc.).
 */
public final class ConfirmationMenu extends BaseMenu {

    private static final int SIZE = 27;
    private static final int CONFIRM_SLOT = 11;
    private static final int CANCEL_SLOT = 15;

    private ConfirmationMenu(GuiLayout layout, Component confirmName, Component cancelName,
                              Runnable onConfirm, Runnable onCancel) {
        super(layout);
        setItem(CONFIRM_SLOT, new GuiItem(
                new ItemBuilder(Material.LIME_WOOL).name(confirmName).build(),
                (player, clickType) -> {
                    player.closeInventory();
                    onConfirm.run();
                }));
        setItem(CANCEL_SLOT, new GuiItem(
                new ItemBuilder(Material.RED_WOOL).name(cancelName).build(),
                (player, clickType) -> {
                    player.closeInventory();
                    if (onCancel != null) {
                        onCancel.run();
                    }
                }));
    }

    public static ConfirmationMenu create(Component title, Component confirmName, Component cancelName,
                                           Runnable onConfirm, Runnable onCancel) {
        ConfigurationSection section = new MemoryConfiguration();
        section.set("size", SIZE);
        GuiLayout layout = GuiLayout.parse(withTitle(section, title));
        return new ConfirmationMenu(layout, confirmName, cancelName, onConfirm, onCancel);
    }

    private static ConfigurationSection withTitle(ConfigurationSection section, Component title) {
        section.set("title", dev.aponder.astracontrol.text.ColorUtil.toMiniMessage(title));
        return section;
    }
}
