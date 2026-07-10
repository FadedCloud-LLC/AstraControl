package dev.aponder.astracontrol.lifecycle;

public record ReloadResult(boolean success, long elapsedMillis, String errorMessage) {

    public static ReloadResult success(long elapsedMillis) {
        return new ReloadResult(true, elapsedMillis, null);
    }

    public static ReloadResult failure(String errorMessage) {
        return new ReloadResult(false, 0L, errorMessage);
    }
}
