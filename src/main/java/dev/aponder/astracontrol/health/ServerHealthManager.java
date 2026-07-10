package dev.aponder.astracontrol.health;

import dev.aponder.astracontrol.config.ConfigManager;
import dev.aponder.astracontrol.language.TranslationKey;
import dev.aponder.astracontrol.scheduler.ScheduledTask;
import dev.aponder.astracontrol.scheduler.SchedulerAdapter;
import dev.aponder.astracontrol.text.TextFormatter;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Periodically captures a {@link ServerHealthSnapshot} and raises edge-triggered
 * warnings (fired once when a threshold is crossed, not once per tick) through a
 * caller-supplied sink so this package stays independent of how warnings are
 * ultimately delivered (chat, GUI, console, etc.).
 */
public final class ServerHealthManager {

    private final ConfigManager config;
    private final SchedulerAdapter scheduler;

    private final TpsMonitor tpsMonitor = new TpsMonitor();
    private final MemoryMonitor memoryMonitor = new MemoryMonitor();
    private final WorldMonitor worldMonitor = new WorldMonitor();
    private final EntityMonitor entityMonitor = new EntityMonitor();
    private final UptimeTracker uptimeTracker = new UptimeTracker();

    private volatile ServerHealthSnapshot lastSnapshot;
    private ScheduledTask task;

    private boolean tpsWarningActive;
    private boolean msptWarningActive;
    private boolean memoryWarningActive;

    public ServerHealthManager(ConfigManager config, SchedulerAdapter scheduler) {
        this.config = config;
        this.scheduler = scheduler;
    }

    public void start(BiConsumer<TranslationKey, Map<String, String>> warningSink) {
        refresh(warningSink);
        long interval = Math.max(20L, config.getHealthUpdateIntervalTicks());
        task = scheduler.runTimer(() -> refresh(warningSink), interval, interval);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public ServerHealthSnapshot currentSnapshot() {
        ServerHealthSnapshot snapshot = lastSnapshot;
        return snapshot != null ? snapshot : refresh(null);
    }

    private ServerHealthSnapshot refresh(BiConsumer<TranslationKey, Map<String, String>> warningSink) {
        ServerHealthSnapshot snapshot = new ServerHealthSnapshot(
                tpsMonitor.tps(),
                tpsMonitor.mspt(),
                tpsMonitor.isMsptAvailable(),
                memoryMonitor.usedBytes(),
                memoryMonitor.freeBytes(),
                memoryMonitor.maxBytes(),
                uptimeTracker.uptime(),
                Bukkit.getOnlinePlayers().size(),
                worldMonitor.loadedWorldCount(),
                worldMonitor.loadedChunkCount(),
                entityMonitor.entityCount(),
                entityMonitor.tileEntityCount(),
                Bukkit.getVersion(),
                System.getProperty("java.version", "unknown"),
                PlatformDetector.displayName(),
                FoliaDetector.isFolia()
        );
        lastSnapshot = snapshot;

        if (warningSink != null && config.isHealthWarningsEnabled()) {
            evaluateWarnings(snapshot, warningSink);
        }
        return snapshot;
    }

    private void evaluateWarnings(ServerHealthSnapshot snapshot, BiConsumer<TranslationKey, Map<String, String>> sink) {
        double currentTps = snapshot.currentTps();
        boolean tpsBad = currentTps >= 0 && currentTps < config.getTpsWarningThreshold();
        if (tpsBad && !tpsWarningActive) {
            sink.accept(TranslationKey.HEALTH_WARNING_TPS, Map.of("tps", TextFormatter.decimal(currentTps, 2)));
        }
        tpsWarningActive = tpsBad;

        boolean msptBad = snapshot.msptAvailable() && snapshot.mspt() > config.getMsptWarningThreshold();
        if (msptBad && !msptWarningActive) {
            sink.accept(TranslationKey.HEALTH_WARNING_MSPT, Map.of("mspt", TextFormatter.decimal(snapshot.mspt(), 2)));
        }
        msptWarningActive = msptBad;

        double usedPercent = snapshot.maxMemoryBytes() > 0
                ? (snapshot.usedMemoryBytes() * 100.0) / snapshot.maxMemoryBytes()
                : 0.0;
        boolean memoryBad = usedPercent > config.getMemoryWarningThresholdPercent();
        if (memoryBad && !memoryWarningActive) {
            sink.accept(TranslationKey.HEALTH_WARNING_MEMORY, Map.of("percent", TextFormatter.decimal(usedPercent, 1)));
        }
        memoryWarningActive = memoryBad;
    }

    public String formatUptime() {
        return TextFormatter.duration(uptimeTracker.uptime());
    }
}
