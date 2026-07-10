package dev.aponder.astracontrol.hooks;

/**
 * Immutable snapshot of a single integration's detection state.
 */
public record HookStatus(
        String id,
        String displayName,
        boolean installed,
        boolean enabled,
        String version,
        String notes
) {

    public static HookStatus missing(String id, String displayName) {
        return new HookStatus(id, displayName, false, false, "-", "Not installed");
    }

    public static HookStatus disabled(String id, String displayName, String version) {
        return new HookStatus(id, displayName, true, false, version, "Installed but not enabled");
    }

    public static HookStatus active(String id, String displayName, String version, String notes) {
        return new HookStatus(id, displayName, true, true, version, notes);
    }
}
