package dev.aponder.astracontrol.gui;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Maps a menu key (e.g. {@code "main"}, {@code "health"}) to a factory that builds a
 * fresh {@link BaseMenu} instance for a given player. Menus are always rebuilt on
 * open so their content reflects live data.
 */
public final class MenuRegistry {

    private final Map<String, Function<Player, BaseMenu>> factories = new ConcurrentHashMap<>();

    public void register(String key, Function<Player, BaseMenu> factory) {
        factories.put(key, factory);
    }

    public Optional<BaseMenu> create(String key, Player player) {
        Function<Player, BaseMenu> factory = factories.get(key);
        return factory == null ? Optional.empty() : Optional.of(factory.apply(player));
    }

    public void clear() {
        factories.clear();
    }
}
