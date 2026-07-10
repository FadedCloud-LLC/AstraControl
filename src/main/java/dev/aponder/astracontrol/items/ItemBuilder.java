package dev.aponder.astracontrol.items;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Small fluent builder over {@link ItemStack}, used for every GUI item so callers
 * never touch {@link org.bukkit.inventory.meta.ItemMeta} directly.
 */
public final class ItemBuilder {

    private final ItemStack itemStack;

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Material material, int amount) {
        this.itemStack = new ItemStack(material, Math.max(1, amount));
    }

    public ItemBuilder name(Component name) {
        ItemMetaUtil.setDisplayName(itemStack, name);
        return this;
    }

    public ItemBuilder lore(List<Component> lore) {
        ItemMetaUtil.setLore(itemStack, lore);
        return this;
    }

    public ItemBuilder customModelData(Integer customModelData) {
        if (customModelData != null) {
            ItemMetaUtil.setCustomModelData(itemStack, customModelData);
        }
        return this;
    }

    public ItemBuilder amount(int amount) {
        itemStack.setAmount(Math.max(1, amount));
        return this;
    }

    public ItemBuilder glow(boolean glow) {
        if (glow) {
            GlowUtil.applyGlow(itemStack);
        }
        return this;
    }

    public ItemStack build() {
        return itemStack;
    }
}
