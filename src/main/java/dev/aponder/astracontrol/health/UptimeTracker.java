package dev.aponder.astracontrol.health;

import java.time.Duration;

/**
 * Tracks how long the plugin (and by extension, this server process) has been
 * running since the last enable.
 */
public final class UptimeTracker {

    private final long startMillis;

    public UptimeTracker() {
        this.startMillis = System.currentTimeMillis();
    }

    public Duration uptime() {
        return Duration.ofMillis(System.currentTimeMillis() - startMillis);
    }
}
