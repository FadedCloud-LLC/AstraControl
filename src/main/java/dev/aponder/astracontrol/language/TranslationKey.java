package dev.aponder.astracontrol.language;

/**
 * Every user-facing message AstraControl can send, paired with a hard-coded English
 * default used when a language file is missing the key entirely.
 */
public enum TranslationKey {

    PREFIX("prefix", "<gray>[<gold>AstraControl</gold><gray>]</gray> "),

    NO_PERMISSION("no-permission", "<red>You do not have permission to do that."),
    PLAYER_ONLY("player-only", "<red>This command can only be used by a player."),
    INVALID_USAGE("invalid-usage", "<red>Invalid usage. <gray>{usage}"),
    UNKNOWN_COMMAND("unknown-command", "<red>Unknown subcommand. <gray>Use /astractrl help."),
    COMMAND_ERROR("command-error", "<red>An internal error occurred while running that command."),
    PLAYER_NOT_FOUND("player-not-found", "<red>No online player named <white>{player}</white> was found."),
    PLUGIN_NOT_FOUND("plugin-not-found", "<red>No plugin named <white>{plugin}</white> was found."),
    COMMAND_ON_COOLDOWN("command-on-cooldown", "<red>Please wait <white>{seconds}s</white> before using this again."),

    RELOAD_STARTED("reload-started", "<yellow>Reloading AstraControl..."),
    RELOAD_SUCCESS("reload-success", "<green>AstraControl reloaded successfully in {time}ms."),
    RELOAD_FAILED("reload-failed", "<red>AstraControl failed to reload: {error}"),

    PLACEHOLDER_MISSING("placeholder-missing", "<red>That placeholder produced no output."),
    PLACEHOLDERAPI_MISSING("placeholderapi-missing", "<red>PlaceholderAPI is not installed. Placeholder testing is unavailable."),

    MAINTENANCE_ENABLED("maintenance-enabled", "<gold>Maintenance mode has been <green>enabled</green>."),
    MAINTENANCE_DISABLED("maintenance-disabled", "<gold>Maintenance mode has been <red>disabled</red>."),
    MAINTENANCE_ALREADY_ENABLED("maintenance-already-enabled", "<yellow>Maintenance mode is already enabled."),
    MAINTENANCE_ALREADY_DISABLED("maintenance-already-disabled", "<yellow>Maintenance mode is already disabled."),
    MAINTENANCE_JOIN_DENIED("maintenance-join-denied", "<red>The server is currently under maintenance.\n<gray>{reason}"),
    MAINTENANCE_STATUS("maintenance-status", "<gray>Maintenance mode is currently <white>{state}</white>."),

    BROADCAST_SENT("broadcast-sent", "<green>Broadcast sent to {count} player(s)."),

    ERRORS_CLEARED("errors-cleared", "<green>Cleared {count} recorded error(s)."),
    ERRORS_EXPORTED("errors-exported", "<green>Exported {count} error(s) to {file}."),
    ERRORS_EMPTY("errors-empty", "<gray>No errors have been recorded."),
    ERROR_NOTIFICATION("error-notification", "<red>[AstraControl] <gray>New error from <white>{source}</white>: {message}"),

    HEALTH_WARNING_TPS("health-warning-tps", "<red>[AstraControl] <gray>TPS dropped to <white>{tps}</white>."),
    HEALTH_WARNING_MSPT("health-warning-mspt", "<red>[AstraControl] <gray>MSPT rose to <white>{mspt}</white>."),
    HEALTH_WARNING_MEMORY("health-warning-memory", "<red>[AstraControl] <gray>Memory usage reached <white>{percent}%</white>."),

    GUI_LOADING("gui-loading", "<gray>Loading..."),
    GUI_NO_ACCESS_ITEM("gui-no-access-item", "<red>You do not have access to this."),

    COMMAND_HELP_HEADER("command-help-header", "<gold>--- AstraControl Help ---"),
    COMMAND_HELP_ENTRY("command-help-entry", "<yellow>/{command} <gray>- {description}"),

    DEBUG_PERMISSION_RESULT("debug-permission-result", "<gray>{player} {result} the permission <white>{permission}</white>."),
    DEBUG_PLAYER_HEADER("debug-player-header", "<gold>--- Debug: {player} ---");

    private final String path;
    private final String defaultValue;

    TranslationKey(String path, String defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    public String path() {
        return path;
    }

    public String defaultValue() {
        return defaultValue;
    }
}
