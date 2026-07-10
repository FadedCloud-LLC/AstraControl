package dev.aponder.astracontrol.items;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * Safely applies enchantments to GUI items, ignoring level restrictions since these
 * items are never meant to be crafted/enchanted normally.
 */
public final class EnchantmentUtil {

    private EnchantmentUtil() {
    }

    public static void addSafely(ItemStack item, Enchantment enchantment, int level) {
        ItemMetaUtil.edit(item, meta -> meta.addEnchant(enchantment, level, true));
    }
}
