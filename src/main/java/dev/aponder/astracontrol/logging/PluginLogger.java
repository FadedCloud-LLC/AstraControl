package dev.aponder.astracontrol.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thin wrapper around the plugin's {@link Logger} so the rest of the codebase never
 * touches {@code java.util.logging} directly.
 */
public final class PluginLogger {

    private final Logger logger;

    public PluginLogger(Logger logger) {
        this.logger = logger;
    }

    public void info(String message) {
        logger.info(message);
    }

    public void warn(String message) {
        logger.warning(message);
    }

    public void error(String message) {
        logger.log(Level.SEVERE, message);
    }

    public void error(String message, Throwable throwable) {
        logger.log(Level.SEVERE, message, throwable);
    }

    public Logger raw() {
        return logger;
    }
}
