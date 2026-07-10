package dev.aponder.astracontrol.hooks;

import dev.aponder.astracontrol.config.HooksConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Common presence/enabled detection shared by every hook. Subclasses may override
 * {@link #extraNotes(Plugin)} to add integration-specific detail (e.g. confirming the
 * integration's API is actually reachable, not just that the plugin is enabled).
 */
public abstract class AbstractHook implements Hook {

    protected final String id;
    private final String defaultDisplayName;
    private final String defaultPluginName;
    private final String defaultNotes;
    protected final HooksConfig hooksConfig;

    protected AbstractHook(String id,
                            String defaultDisplayName,
                            String defaultPluginName,
                            String defaultNotes,
                            HooksConfig hooksConfig) {
        this.id = id;
        this.defaultDisplayName = defaultDisplayName;
        this.defaultPluginName = defaultPluginName;
        this.defaultNotes = defaultNotes;
        this.hooksConfig = hooksConfig;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String displayName() {
        return hooksConfig.displayName(id, defaultDisplayName);
    }

    protected String pluginName() {
        return hooksConfig.pluginName(id, defaultPluginName);
    }

    @Override
    public HookStatus detect() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName());
        if (plugin == null) {
            return HookStatus.missing(id, displayName());
        }
        if (!plugin.isEnabled()) {
            return HookStatus.disabled(id, displayName(), plugin.getDescription().getVersion());
        }
        return HookStatus.active(id, displayName(), plugin.getDescription().getVersion(), extraNotes(plugin));
    }

    protected String extraNotes(Plugin plugin) {
        return hooksConfig.notes(id, defaultNotes);
    }
}
