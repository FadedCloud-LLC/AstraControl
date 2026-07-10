package dev.aponder.astracontrol.errors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Captures WARN/ERROR log events server-wide via a lightweight Log4j2 appender rather
 * than tailing the log file, so this works regardless of file rotation, encoding, or
 * console redirection. Every append is defensive - a failure to record must never
 * crash the logging pipeline itself.
 */
public final class ErrorWatcher extends AbstractAppender {

    private final ErrorBuffer buffer;
    private final Consumer<ErrorRecord> onNewOrRepeatedError;
    private final Map<String, String> sourceCache = new ConcurrentHashMap<>();
    private volatile boolean attached;

    public ErrorWatcher(ErrorBuffer buffer, Consumer<ErrorRecord> onNewOrRepeatedError) {
        super("AstraControlErrorWatcher", null, null, true, Property.EMPTY_ARRAY);
        this.buffer = buffer;
        this.onNewOrRepeatedError = onNewOrRepeatedError;
    }

    @Override
    public void append(LogEvent event) {
        try {
            if (!event.getLevel().isMoreSpecificThan(Level.WARN)) {
                return;
            }
            String message = event.getMessage() == null ? "" : event.getMessage().getFormattedMessage();
            if (message.isBlank()) {
                return;
            }

            String source = resolveSource(event.getLoggerName());
            String stackTrace = event.getThrown() != null ? stackTraceOf(event.getThrown()) : null;

            ErrorRecord record = buffer.record(source, event.getLevel().name(), message, stackTrace);
            onNewOrRepeatedError.accept(record);
        } catch (Exception ignored) {
            // The error watcher must never throw from inside the logging pipeline.
        }
    }

    public void attachToRootLogger() {
        if (attached) {
            return;
        }
        start();
        Logger rootLogger = (Logger) LogManager.getRootLogger();
        rootLogger.addAppender(this);
        attached = true;
    }

    public void detachFromRootLogger() {
        if (!attached) {
            return;
        }
        Logger rootLogger = (Logger) LogManager.getRootLogger();
        rootLogger.removeAppender(this);
        stop();
        attached = false;
    }

    private String resolveSource(String loggerName) {
        if (loggerName == null || loggerName.isBlank()) {
            return "console";
        }
        return sourceCache.computeIfAbsent(loggerName, this::findPluginNameFor);
    }

    private String findPluginNameFor(String loggerName) {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getClass().getName().equals(loggerName)) {
                return plugin.getName();
            }
        }
        return loggerName;
    }

    private String stackTraceOf(Throwable throwable) {
        StringBuilder builder = new StringBuilder();
        builder.append(throwable);
        for (StackTraceElement element : throwable.getStackTrace()) {
            builder.append(System.lineSeparator()).append("    at ").append(element);
        }
        return builder.toString();
    }
}
