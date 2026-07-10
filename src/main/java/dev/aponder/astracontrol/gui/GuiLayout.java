package dev.aponder.astracontrol.gui;

import dev.aponder.astracontrol.items.ConfigItemBuilder;
import dev.aponder.astracontrol.text.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * The structural (non-content) parts of a menu, parsed once from its {@code gui.yml}
 * section: title, size, filler item, and the well-known navigation slots.
 */
public final class GuiLayout {

    private final Component title;
    private final int size;
    private final ItemStack filler;
    private final Integer backSlot;
    private final Integer previousPageSlot;
    private final Integer nextPageSlot;
    private final Integer contentStart;
    private final Integer contentEnd;

    private GuiLayout(Component title, int size, ItemStack filler, Integer backSlot,
                       Integer previousPageSlot, Integer nextPageSlot, Integer contentStart, Integer contentEnd) {
        this.title = title;
        this.size = size;
        this.filler = filler;
        this.backSlot = backSlot;
        this.previousPageSlot = previousPageSlot;
        this.nextPageSlot = nextPageSlot;
        this.contentStart = contentStart;
        this.contentEnd = contentEnd;
    }

    public static GuiLayout parse(ConfigurationSection section) {
        if (section == null) {
            section = new MemoryConfiguration();
        }
        Component title = MessageUtil.render(section.getString("title", "<gray>Menu"), Map.of());
        int size = section.getInt("size", 27);

        ItemStack filler = null;
        ConfigurationSection fillerSection = section.getConfigurationSection("filler");
        if (fillerSection != null) {
            filler = ConfigItemBuilder.build(fillerSection, Map.of());
        }

        Integer backSlot = section.isInt("back-slot") ? section.getInt("back-slot") : null;
        Integer previousPageSlot = section.isInt("previous-page-slot") ? section.getInt("previous-page-slot") : null;
        Integer nextPageSlot = section.isInt("next-page-slot") ? section.getInt("next-page-slot") : null;
        Integer contentStart = section.isInt("content-slots-start") ? section.getInt("content-slots-start") : null;
        Integer contentEnd = section.isInt("content-slots-end") ? section.getInt("content-slots-end") : null;

        return new GuiLayout(title, size, filler, backSlot, previousPageSlot, nextPageSlot, contentStart, contentEnd);
    }

    public Component title() {
        return title;
    }

    public int size() {
        return size;
    }

    public ItemStack filler() {
        return filler == null ? null : filler.clone();
    }

    public Integer backSlot() {
        return backSlot;
    }

    public Integer previousPageSlot() {
        return previousPageSlot;
    }

    public Integer nextPageSlot() {
        return nextPageSlot;
    }

    public Integer contentStart() {
        return contentStart;
    }

    public Integer contentEnd() {
        return contentEnd;
    }
}
