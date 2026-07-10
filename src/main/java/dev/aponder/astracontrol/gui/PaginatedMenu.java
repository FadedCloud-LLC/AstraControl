package dev.aponder.astracontrol.gui;

import dev.aponder.astracontrol.items.ItemBuilder;
import dev.aponder.astracontrol.text.MessageUtil;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.Map;

/**
 * A {@link BaseMenu} whose content slots (defined by {@code content-slots-start}/
 * {@code content-slots-end} in {@code gui.yml}) are paged, with optional
 * previous/next buttons at the configured slots.
 */
public abstract class PaginatedMenu extends BaseMenu {

    private final int contentStart;
    private final int contentEnd;
    private List<GuiItem> content = List.of();
    private int page = 0;

    protected PaginatedMenu(GuiLayout layout) {
        super(layout);
        this.contentStart = layout.contentStart() != null ? layout.contentStart() : 0;
        this.contentEnd = layout.contentEnd() != null ? layout.contentEnd() : layout.size() - 1;
    }

    private int pageSize() {
        return Math.max(1, contentEnd - contentStart + 1);
    }

    public void setContent(List<GuiItem> content) {
        this.content = content;
        this.page = 0;
        render();
    }

    public boolean hasNextPage() {
        return (page + 1) * pageSize() < content.size();
    }

    public boolean hasPreviousPage() {
        return page > 0;
    }

    public void nextPage() {
        if (hasNextPage()) {
            page++;
            render();
        }
    }

    public void previousPage() {
        if (hasPreviousPage()) {
            page--;
            render();
        }
    }

    protected void render() {
        int size = pageSize();
        int start = page * size;

        for (int i = 0; i < size; i++) {
            int slot = contentStart + i;
            int index = start + i;
            if (index < content.size()) {
                setItem(slot, content.get(index));
            } else {
                clearItem(slot);
            }
        }

        Integer previousPageSlot = layout.previousPageSlot();
        if (previousPageSlot != null) {
            if (hasPreviousPage()) {
                setItem(previousPageSlot, previousPageItem());
            } else {
                clearItem(previousPageSlot);
            }
        }

        Integer nextPageSlot = layout.nextPageSlot();
        if (nextPageSlot != null) {
            if (hasNextPage()) {
                setItem(nextPageSlot, nextPageItem());
            } else {
                clearItem(nextPageSlot);
            }
        }
    }

    protected GuiItem previousPageItem() {
        return new GuiItem(new ItemBuilder(Material.ARROW)
                .name(MessageUtil.render("<gray>« Previous Page", Map.of())).build(),
                (player, click) -> previousPage());
    }

    protected GuiItem nextPageItem() {
        return new GuiItem(new ItemBuilder(Material.ARROW)
                .name(MessageUtil.render("<gray>Next Page »", Map.of())).build(),
                (player, click) -> nextPage());
    }

    @Override
    public void handleClick(org.bukkit.entity.Player player, int slot, ClickType clickType) {
        Integer previousPageSlot = layout.previousPageSlot();
        Integer nextPageSlot = layout.nextPageSlot();
        if (previousPageSlot != null && slot == previousPageSlot) {
            previousPage();
            return;
        }
        if (nextPageSlot != null && slot == nextPageSlot) {
            nextPage();
            return;
        }
        super.handleClick(player, slot, clickType);
    }
}
