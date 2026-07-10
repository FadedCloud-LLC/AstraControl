package dev.aponder.astracontrol.items;

import dev.aponder.astracontrol.text.MessageUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;

/**
 * Builds an {@link org.bukkit.inventory.ItemStack} entirely from a {@code gui.yml}
 * item section - material, display name, lore, custom-model-data, glow, and amount
 * are all configurable, matching AstraControl's "no hardcoded GUI layouts" rule.
 */
public final class ConfigItemBuilder {

    private ConfigItemBuilder() {
    }

    public static org.bukkit.inventory.ItemStack build(ConfigurationSection section, Map<String, String> placeholders) {
        Material material = resolveMaterial(section.getString("material", "STONE"));
        int amount = section.getInt("amount", 1);

        ItemBuilder builder = new ItemBuilder(material, amount);

        String name = section.getString("name");
        if (name != null) {
            builder.name(MessageUtil.render(name, placeholders));
        }

        List<String> lore = section.getStringList("lore");
        if (!lore.isEmpty()) {
            builder.lore(MessageUtil.renderLines(lore, placeholders));
        }

        if (section.isInt("custom-model-data")) {
            builder.customModelData(section.getInt("custom-model-data"));
        }

        if (section.getBoolean("glow", false)) {
            builder.glow(true);
        }

        return builder.build();
    }

    private static Material resolveMaterial(String name) {
        Material material = Material.matchMaterial(name);
        return material != null ? material : Material.BARRIER;
    }
}
