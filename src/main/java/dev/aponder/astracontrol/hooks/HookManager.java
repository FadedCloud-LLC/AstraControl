package dev.aponder.astracontrol.hooks;

import dev.aponder.astracontrol.config.HooksConfig;
import dev.aponder.astracontrol.logging.DebugLogger;
import dev.aponder.astracontrol.logging.HookStatusRegistry;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Detects every known optional integration and publishes the results to a
 * {@link HookStatusRegistry}. Disabling a hook in {@code hooks.yml} skips its check
 * entirely - AstraControl never unloads or reloads another plugin.
 *
 * <p>Beyond the dedicated {@link Hook} classes below (which exist for integrations
 * AstraControl does something special with), any additional entry a server owner adds
 * under {@code hooks.yml}'s {@code hooks:} section is detected generically via
 * {@link GenericHook} - watching a new plugin never requires an AstraControl code
 * change or release.
 */
public final class HookManager {

    private final HooksConfig hooksConfig;
    private final HookStatusRegistry registry;
    private final DebugLogger debugLogger;
    private final List<Hook> knownHooks;
    private final Set<String> knownHookIds;

    public HookManager(HooksConfig hooksConfig, HookStatusRegistry registry, DebugLogger debugLogger) {
        this.hooksConfig = hooksConfig;
        this.registry = registry;
        this.debugLogger = debugLogger;
        this.knownHooks = List.of(
                new PlaceholderAPIHook(hooksConfig),
                new LuckPermsHook(hooksConfig),
                new VaultHook(hooksConfig),
                new ProtocolLibHook(hooksConfig),
                new WorldGuardHook(hooksConfig),
                new CitizensHook(hooksConfig),
                new ItemsAdderHook(hooksConfig),
                new OraxenHook(hooksConfig),
                new NexoHook(hooksConfig),
                new HeadDatabaseHook(hooksConfig),
                new PlayerPointsHook(hooksConfig),
                new ViaVersionHook(hooksConfig),
                new TabHook(hooksConfig),
                new EssentialsHook(hooksConfig)
        );
        this.knownHookIds = knownHooks.stream().map(Hook::id).collect(Collectors.toUnmodifiableSet());
    }

    public void detectAll() {
        registry.clear();
        for (Hook hook : knownHooks) {
            detectAndPublish(hook);
        }
        for (String id : hooksConfig.additionalHookIds(knownHookIds)) {
            detectAndPublish(new GenericHook(id, hooksConfig));
        }
    }

    private void detectAndPublish(Hook hook) {
        if (!hooksConfig.isEnabled(hook.id())) {
            debugLogger.debug("Skipping hook check for '" + hook.id() + "' (disabled in hooks.yml)");
            return;
        }
        HookStatus status;
        try {
            status = hook.detect();
        } catch (Exception e) {
            status = HookStatus.missing(hook.id(), hook.displayName());
            debugLogger.debug("Hook detection threw for '" + hook.id() + "': " + e.getMessage());
        }
        registry.put(status);
        if (!status.installed() && hooksConfig.showMissingInConsole()) {
            debugLogger.debug(status.displayName() + " is not installed.");
        }
    }

    public HookStatusRegistry registry() {
        return registry;
    }
}
