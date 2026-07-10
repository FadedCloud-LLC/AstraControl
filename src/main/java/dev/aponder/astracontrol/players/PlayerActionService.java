package dev.aponder.astracontrol.players;

import dev.aponder.astracontrol.config.ConfigManager;
import dev.aponder.astracontrol.scheduler.SchedulerAdapter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

/**
 * Performs the staff actions offered by the Online Player Tools GUI. Teleports use
 * Paper's {@code teleportAsync}, which is safe on both classic Bukkit/Paper and
 * Folia. Every action is expected to already be permission-checked by the caller.
 */
public final class PlayerActionService {

    private final ConfigManager config;
    private final SchedulerAdapter scheduler;

    public PlayerActionService(ConfigManager config, SchedulerAdapter scheduler) {
        this.config = config;
        this.scheduler = scheduler;
    }

    public CompletableFuture<Boolean> teleportToPlayer(Player staff, Player target) {
        if (!config.isTeleportAllowed()) {
            return CompletableFuture.completedFuture(false);
        }
        return staff.teleportAsync(target.getLocation());
    }

    public CompletableFuture<Boolean> teleportPlayerHere(Player staff, Player target) {
        if (!config.isTeleportAllowed()) {
            return CompletableFuture.completedFuture(false);
        }
        return target.teleportAsync(staff.getLocation());
    }

    public void sendMessage(Player target, Component message) {
        target.sendMessage(message);
    }

    public boolean openInventory(Player staff, Player target) {
        if (!config.isInventoryViewAllowed()) {
            return false;
        }
        staff.openInventory(target.getInventory());
        return true;
    }

    public boolean runConfiguredStaffCommand(Player target) {
        String template = config.getStaffCommandTemplate();
        if (template == null || template.isBlank()) {
            return false;
        }
        String command = template.replace("{player}", target.getName());
        scheduler.runNow(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
        return true;
    }
}
