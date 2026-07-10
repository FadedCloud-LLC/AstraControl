package dev.aponder.astracontrol.logging;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Raises the log level of noisy shaded/bundled library loggers so they never spam the
 * console. AstraControl's own logger is never touched by this class.
 */
public final class LibraryLogSilencer {

    private static final List<String> SILENCED_LOGGERS = List.of(
            "org.bstats"
    );

    private LibraryLogSilencer() {
    }

    public static void apply() {
        for (String name : SILENCED_LOGGERS) {
            Logger.getLogger(name).setLevel(Level.WARNING);
        }
    }
}
