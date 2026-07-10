package dev.aponder.astracontrol.gui;

import dev.aponder.astracontrol.items.ConfigItemBuilder;
import dev.aponder.astracontrol.items.ItemBuilder;
import dev.aponder.astracontrol.language.LanguageManager;
import dev.aponder.astracontrol.language.TranslationKey;
import dev.aponder.astracontrol.text.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Base type for every AstraControl menu. Holds its own {@link Inventory} (as its
 * {@link InventoryHolder}) so {@link MenuManager} can identify AstraControl
 * inventories generically and enforce duplication-safe click handling.
 */
public abstract class BaseMenu implements InventoryHolder {

    protected final Inventory inventory;
    protected final Map<Integer, GuiItem> items = new HashMap<>();
    protected final GuiLayout layout;

    protected BaseMenu(GuiLayout layout) {
        this.layout = layout;
        this.inventory = Bukkit.createInventory(this, layout.size(), layout.title());
        applyFiller();
    }

    private void applyFiller() {
        ItemStack filler = layout.filler();
        if (filler == null) {
            return;
        }
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, filler.clone());
        }
    }

    protected void setItem(int slot, GuiItem item) {
        if (slot < 0 || slot >= inventory.getSize()) {
            return;
        }
        items.put(slot, item);
        inventory.setItem(slot, item.itemStack());
    }

    protected void clearItem(int slot) {
        items.remove(slot);
        ItemStack filler = layout.filler();
        inventory.setItem(slot, filler);
    }

    /**
     * Reads {@code items.<itemKey>} from {@code menuSection}, builds its item via
     * {@link ConfigItemBuilder}, and binds a permission-gated click handler to it.
     * Pass a {@code null} permission for actions everyone who can open the menu may
     * use (e.g. "close").
     */
    protected void bindConfigButton(ConfigurationSection menuSection,
                                     String itemKey,
                                     String permission,
                                     LanguageManager language,
                                     BiConsumer<Player, ClickType> action) {
        bindConfigButton(menuSection, itemKey, permission, language, Map.of(), action);
    }

    protected void bindConfigButton(ConfigurationSection menuSection,
                                     String itemKey,
                                     String permission,
                                     LanguageManager language,
                                     Map<String, String> placeholders,
                                     BiConsumer<Player, ClickType> action) {
        if (menuSection == null) {
            return;
        }
        ConfigurationSection itemSection = menuSection.getConfigurationSection("items." + itemKey);
        if (itemSection == null) {
            return;
        }
        int slot = itemSection.getInt("slot");
        ItemStack item = ConfigItemBuilder.build(itemSection, placeholders);
        setItem(slot, new GuiItem(item, (player, clickType) -> {
            if (permission != null && !player.hasPermission(permission)) {
                language.send(player, TranslationKey.NO_PERMISSION);
                return;
            }
            action.accept(player, clickType);
        }));
    }

    protected void bindDecoration(ConfigurationSection menuSection, String itemKey, Map<String, String> placeholders) {
        if (menuSection == null) {
            return;
        }
        ConfigurationSection itemSection = menuSection.getConfigurationSection("items." + itemKey);
        if (itemSection == null) {
            return;
        }
        int slot = itemSection.getInt("slot");
        setItem(slot, new GuiItem(ConfigItemBuilder.build(itemSection, placeholders)));
    }

    protected void bindBackButton(GuiContext ctx, String targetMenuKey) {
        Integer backSlot = layout.backSlot();
        if (backSlot == null) {
            return;
        }
        ItemStack item = new ItemBuilder(Material.ARROW)
                .name(MessageUtil.render("<gray>« Back", Map.of()))
                .build();
        setItem(backSlot, new GuiItem(item, (player, clickType) -> ctx.guiManager().open(player, targetMenuKey)));
    }

    public void handleClick(Player player, int slot, ClickType clickType) {
        GuiItem item = items.get(slot);
        if (item != null) {
            item.handleClick(player, clickType);
        }
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public GuiLayout layout() {
        return layout;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
