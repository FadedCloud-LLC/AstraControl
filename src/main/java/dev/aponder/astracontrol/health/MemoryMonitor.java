package dev.aponder.astracontrol.health;

/**
 * Reads JVM heap memory usage. No allocation-heavy work is performed here.
 */
public final class MemoryMonitor {

    public long usedBytes() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    public long freeBytes() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.freeMemory();
    }

    public long maxBytes() {
        return Runtime.getRuntime().maxMemory();
    }

    public long totalBytes() {
        return Runtime.getRuntime().totalMemory();
    }

    public double usedPercentOfMax() {
        long max = maxBytes();
        if (max <= 0) {
            return 0.0;
        }
        return (usedBytes() * 100.0) / max;
    }
}
