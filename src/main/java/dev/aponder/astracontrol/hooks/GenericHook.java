package dev.aponder.astracontrol.hooks;

import dev.aponder.astracontrol.config.HooksConfig;

/**
 * A {@code hooks.yml} entry with no dedicated {@link Hook} class. Lets a server owner
 * watch any installed plugin's presence/enabled/version purely by adding an entry to
 * {@code hooks.yml} - no AstraControl code change or release required. Limited to the
 * generic detection {@link AbstractHook} already provides; integrations that need
 * deeper, API-specific verification (e.g. actually calling another plugin's API)
 * should get a dedicated {@link AbstractHook} subclass instead.
 */
public final class GenericHook extends AbstractHook {

    public GenericHook(String id, HooksConfig hooksConfig) {
        super(id, id, id, "Configured in hooks.yml.", hooksConfig);
    }
}
