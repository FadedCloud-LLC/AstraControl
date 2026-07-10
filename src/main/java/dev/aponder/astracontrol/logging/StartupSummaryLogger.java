package dev.aponder.astracontrol.logging;

import dev.aponder.astracontrol.hooks.HookStatus;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Prints the clean, single-glance startup banner. Detailed hook/diagnostic detail is
 * left to {@link DebugLogger} when {@code debug: true}.
 */
public final class StartupSummaryLogger {

    private final PluginLogger logger;

    public StartupSummaryLogger(PluginLogger logger) {
        this.logger = logger;
    }

    public void printEnabled(String version,
                              String platform,
                              Collection<HookStatus> activeHooks,
                              boolean metricsEnabled,
                              boolean maintenanceEnabled) {
        String hooks = activeHooks.isEmpty()
                ? "None"
                : activeHooks.stream().map(HookStatus::displayName).collect(Collectors.joining(", "));

        logger.info("Enabled successfully.");
        logger.info("Version: " + version);
        logger.info("Platform: " + platform);
        logger.info("Hooks: " + hooks);
        logger.info("Metrics: " + (metricsEnabled ? "Enabled" : "Disabled"));
        logger.info("Maintenance: " + (maintenanceEnabled ? "Enabled" : "Disabled"));
    }

    public void printDisabled() {
        logger.info("Disabled.");
    }
}
