package dev.aponder.astracontrol.items;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.function.Consumer;

/**
 * Small helpers around {@link ItemMeta} mutation so callers don't repeat the
 * null-check/apply-back dance for every field.
 */
public final class ItemMetaUtil {

    private ItemMetaUtil() {
    }

    public static void edit(ItemStack item, Consumer<ItemMeta> editor) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        editor.accept(meta);
        item.setItemMeta(meta);
    }

    public static void setDisplayName(ItemStack item, Component name) {
        edit(item, meta -> meta.displayName(name));
    }

    public static void setLore(ItemStack item, List<Component> lore) {
        edit(item, meta -> meta.lore(lore));
    }

    public static void setCustomModelData(ItemStack item, int customModelData) {
        edit(item, meta -> meta.setCustomModelData(customModelData));
    }

    public static void setUnbreakable(ItemStack item, boolean unbreakable) {
        edit(item, meta -> meta.setUnbreakable(unbreakable));
    }
}
