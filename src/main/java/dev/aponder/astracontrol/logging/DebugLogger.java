package dev.aponder.astracontrol.logging;

import java.util.function.BooleanSupplier;

/**
 * Logs verbose diagnostic detail only when {@code config.yml}'s {@code debug} flag is
 * enabled. Decoupled from the config system via a {@link BooleanSupplier} so this
 * package never depends on {@code dev.aponder.astracontrol.config}.
 */
public final class DebugLogger {

    private final PluginLogger logger;
    private final BooleanSupplier debugEnabled;

    public DebugLogger(PluginLogger logger, BooleanSupplier debugEnabled) {
        this.logger = logger;
        this.debugEnabled = debugEnabled;
    }

    public void debug(String message) {
        if (debugEnabled.getAsBoolean()) {
            logger.info("[debug] " + message);
        }
    }

    public void debug(String message, Throwable throwable) {
        if (debugEnabled.getAsBoolean()) {
            logger.error("[debug] " + message, throwable);
        }
    }

    public boolean isDebugEnabled() {
        return debugEnabled.getAsBoolean();
    }
}
