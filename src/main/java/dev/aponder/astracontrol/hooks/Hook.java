package dev.aponder.astracontrol.hooks;

/**
 * A single optional integration AstraControl can detect. Implementations must never
 * throw - detection failures should be reflected in the returned {@link HookStatus}.
 */
public interface Hook {

    String id();

    String displayName();

    HookStatus detect();
}
