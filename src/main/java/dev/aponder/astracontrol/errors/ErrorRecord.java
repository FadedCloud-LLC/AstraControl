package dev.aponder.astracontrol.errors;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A deduplicated warning/error. Repeated occurrences of the same source+message
 * increment {@link #count()} and bump {@link #lastSeenMillis()} rather than creating
 * a new entry, so a noisy repeating warning cannot flood the buffer.
 */
public final class ErrorRecord {

    private final String source;
    private final String level;
    private final String message;
    private final String stackTrace;
    private final long firstSeenMillis;
    private volatile long lastSeenMillis;
    private final AtomicInteger count = new AtomicInteger(1);

    public ErrorRecord(String source, String level, String message, String stackTrace) {
        this.source = source;
        this.level = level;
        this.message = message;
        this.stackTrace = stackTrace;
        this.firstSeenMillis = System.currentTimeMillis();
        this.lastSeenMillis = firstSeenMillis;
    }

    public void touch() {
        count.incrementAndGet();
        lastSeenMillis = System.currentTimeMillis();
    }

    public String source() {
        return source;
    }

    public String level() {
        return level;
    }

    public String message() {
        return message;
    }

    public String stackTrace() {
        return stackTrace;
    }

    public boolean hasStackTrace() {
        return stackTrace != null && !stackTrace.isBlank();
    }

    public long firstSeenMillis() {
        return firstSeenMillis;
    }

    public long lastSeenMillis() {
        return lastSeenMillis;
    }

    public int count() {
        return count.get();
    }

    public static String dedupeKey(String source, String level, String message) {
        return source + "|" + level + "|" + message;
    }
}
