package dev.aponder.astracontrol.hooks;

import dev.aponder.astracontrol.config.HooksConfig;
import org.bukkit.plugin.Plugin;

public final class PlaceholderAPIHook extends AbstractHook {

    public PlaceholderAPIHook(HooksConfig hooksConfig) {
        super("placeholderapi", "PlaceholderAPI", "PlaceholderAPI",
                "Enables the placeholder tester and placeholder rendering in broadcasts.", hooksConfig);
    }

    @Override
    protected String extraNotes(Plugin plugin) {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            return super.extraNotes(plugin);
        } catch (ClassNotFoundException e) {
            return "Plugin present but API class not found.";
        }
    }
}
