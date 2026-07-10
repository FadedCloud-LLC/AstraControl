package dev.aponder.astracontrol.broadcast;

import dev.aponder.astracontrol.config.ConfigManager;
import dev.aponder.astracontrol.scheduler.SchedulerAdapter;
import dev.aponder.astracontrol.text.MessageUtil;
import dev.aponder.astracontrol.text.PlaceholderUtil;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Map;

/**
 * Dispatches admin-triggered broadcasts (chat, title, actionbar, bossbar) to every
 * online player. Message text is treated as trusted MiniMessage input since these
 * actions are permission-gated to staff; per-player PlaceholderAPI substitution
 * happens before formatting.
 */
public final class BroadcastManager {

    private final ConfigManager config;
    private final SchedulerAdapter scheduler;

    public BroadcastManager(ConfigManager config, SchedulerAdapter scheduler) {
        this.config = config;
        this.scheduler = scheduler;
    }

    public int execute(BroadcastRequest request) {
        return switch (request.type()) {
            case CHAT -> broadcastChat(request.message());
            case TITLE -> broadcastTitle(request.message(), request.subtitle());
            case ACTIONBAR -> broadcastActionbar(request.message());
            case BOSSBAR -> broadcastBossbar(request.message());
        };
    }

    public int broadcastChat(String rawMessage) {
        int count = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            String withPlaceholders = PlaceholderUtil.apply(player, rawMessage);
            Component component = MessageUtil.renderMixed(
                    config.getBroadcastChatFormat(), Map.of(), Map.of("message", withPlaceholders));
            scheduler.runAtEntity(player, () -> player.sendMessage(component));
            count++;
        }
        playAccompanimentSound();
        return count;
    }

    public int broadcastTitle(String title, String subtitle) {
        int count = 0;
        Title.Times times = Title.Times.times(
                Duration.ofMillis(config.getBroadcastTitleFadeIn() * 50L),
                Duration.ofMillis(config.getBroadcastTitleStay() * 50L),
                Duration.ofMillis(config.getBroadcastTitleFadeOut() * 50L));

        for (Player player : Bukkit.getOnlinePlayers()) {
            String titleText = PlaceholderUtil.apply(player, title);
            String subtitleText = subtitle == null ? "" : PlaceholderUtil.apply(player, subtitle);
            Title rendered = Title.title(
                    MessageUtil.render(titleText, Map.of()),
                    MessageUtil.render(subtitleText, Map.of()),
                    times);
            scheduler.runAtEntity(player, () -> player.showTitle(rendered));
            count++;
        }
        playAccompanimentSound();
        return count;
    }

    public int broadcastActionbar(String message) {
        int count = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            String withPlaceholders = PlaceholderUtil.apply(player, message);
            Component component = MessageUtil.render(withPlaceholders, Map.of());
            scheduler.runAtEntity(player, () -> player.sendActionBar(component));
            count++;
        }
        playAccompanimentSound();
        return count;
    }

    public int broadcastBossbar(String message) {
        if (!config.isBossbarEnabled()) {
            return 0;
        }
        int count = 0;
        BossBar.Color color = parseColor(config.getBossbarColor());
        BossBar.Overlay overlay = parseOverlay(config.getBossbarOverlay());
        long delayTicks = Math.max(1L, config.getBossbarDurationSeconds() * 20L);

        for (Player player : Bukkit.getOnlinePlayers()) {
            String withPlaceholders = PlaceholderUtil.apply(player, message);
            BossBar bossBar = BossBar.bossBar(MessageUtil.render(withPlaceholders, Map.of()), 1.0f, color, overlay);
            scheduler.runAtEntity(player, () -> player.showBossBar(bossBar));
            scheduler.runAtEntityLater(player, () -> {
                if (player.isOnline()) {
                    player.hideBossBar(bossBar);
                }
            }, delayTicks);
            count++;
        }
        return count;
    }

    private void playAccompanimentSound() {
        if (!config.isBroadcastSoundEnabled()) {
            return;
        }
        String sound = config.getBroadcastSound();
        float volume = config.getBroadcastSoundVolume();
        float pitch = config.getBroadcastSoundPitch();
        for (Player player : Bukkit.getOnlinePlayers()) {
            scheduler.runAtEntity(player, () -> player.playSound(player.getLocation(), sound, volume, pitch));
        }
    }

    private BossBar.Color parseColor(String value) {
        try {
            return BossBar.Color.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BossBar.Color.YELLOW;
        }
    }

    private BossBar.Overlay parseOverlay(String value) {
        try {
            return BossBar.Overlay.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BossBar.Overlay.PROGRESS;
        }
    }
}
