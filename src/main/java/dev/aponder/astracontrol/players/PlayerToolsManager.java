package dev.aponder.astracontrol.players;

import dev.aponder.astracontrol.util.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Builds {@link PlayerSnapshot}s from live {@link Player} objects using only public,
 * safe Bukkit/Paper API. Ping and client-brand lookups are Paper-only additions, so
 * they are resolved reflectively once and simply report as "unavailable" on Spigot.
 */
public final class PlayerToolsManager {

    private static final Method GET_PING = ReflectionUtil.findMethod(Player.class, "getPing");
    private static final Method GET_CLIENT_BRAND = ReflectionUtil.findMethod(Player.class, "getClientBrandName");

    public List<Player> onlinePlayers() {
        return List.copyOf(Bukkit.getOnlinePlayers());
    }

    public PlayerSnapshot snapshot(Player player) {
        Location location = player.getLocation();
        return new PlayerSnapshot(
                player.getName(),
                player.getUniqueId(),
                location.getWorld() != null ? location.getWorld().getName() : "unknown",
                location.getX(),
                location.getY(),
                location.getZ(),
                readPing(player),
                player.getGameMode().name(),
                player.getHealth(),
                player.getFoodLevel(),
                player.isOp(),
                readClientBrand(player)
        );
    }

    private int readPing(Player player) {
        if (GET_PING == null) {
            return -1;
        }
        try {
            return (int) GET_PING.invoke(player);
        } catch (ReflectiveOperationException e) {
            return -1;
        }
    }

    private String readClientBrand(Player player) {
        if (GET_CLIENT_BRAND == null) {
            return null;
        }
        try {
            return (String) GET_CLIENT_BRAND.invoke(player);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }
}
