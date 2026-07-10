package dev.aponder.astracontrol.broadcast;

/**
 * A single broadcast to be dispatched by {@link BroadcastManager}.
 */
public record BroadcastRequest(BroadcastType type, String message, String subtitle) {

    public static BroadcastRequest chat(String message) {
        return new BroadcastRequest(BroadcastType.CHAT, message, null);
    }

    public static BroadcastRequest title(String title, String subtitle) {
        return new BroadcastRequest(BroadcastType.TITLE, title, subtitle);
    }

    public static BroadcastRequest actionbar(String message) {
        return new BroadcastRequest(BroadcastType.ACTIONBAR, message, null);
    }

    public static BroadcastRequest bossbar(String message) {
        return new BroadcastRequest(BroadcastType.BOSSBAR, message, null);
    }
}
