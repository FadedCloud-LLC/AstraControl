package dev.aponder.astracontrol.items;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

/**
 * Applies the "glowing" GUI item effect: an enchantment for the glint, hidden from
 * the tooltip via {@link ItemFlag#HIDE_ENCHANTS}.
 */
public final class GlowUtil {

    private GlowUtil() {
    }

    public static void applyGlow(ItemStack item) {
        EnchantmentUtil.addSafely(item, Enchantment.LUCK, 1);
        ItemMetaUtil.edit(item, meta -> meta.addItemFlags(ItemFlag.HIDE_ENCHANTS));
    }
}
