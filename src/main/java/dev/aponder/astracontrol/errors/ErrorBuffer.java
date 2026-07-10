package dev.aponder.astracontrol.errors;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Bounded, deduplicated, thread-safe store of {@link ErrorRecord}s. Oldest entries are
 * evicted once {@code maxSize} is reached.
 */
public final class ErrorBuffer {

    private final Map<String, ErrorRecord> records = new LinkedHashMap<>();
    private final Object lock = new Object();
    private volatile int maxSize;

    public ErrorBuffer(int maxSize) {
        this.maxSize = Math.max(1, maxSize);
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = Math.max(1, maxSize);
    }

    public ErrorRecord record(String source, String level, String message, String stackTrace) {
        String key = ErrorRecord.dedupeKey(source, level, message);
        synchronized (lock) {
            ErrorRecord existing = records.get(key);
            if (existing != null) {
                existing.touch();
                return existing;
            }
            if (records.size() >= maxSize) {
                Iterator<String> iterator = records.keySet().iterator();
                if (iterator.hasNext()) {
                    iterator.next();
                    iterator.remove();
                }
            }
            ErrorRecord created = new ErrorRecord(source, level, message, stackTrace);
            records.put(key, created);
            return created;
        }
    }

    public List<ErrorRecord> snapshot() {
        synchronized (lock) {
            return List.copyOf(records.values());
        }
    }

    public void clear() {
        synchronized (lock) {
            records.clear();
        }
    }

    public int size() {
        synchronized (lock) {
            return records.size();
        }
    }
}
