package dev.aponder.astracontrol.maintenance;

/**
 * Plain snapshot of the current maintenance state, handed out by
 * {@link MaintenanceManager} for display in the GUI/commands.
 */
public record MaintenanceState(boolean enabled, String reason) {
}
