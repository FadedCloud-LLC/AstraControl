package dev.aponder.astracontrol.hooks;

import dev.aponder.astracontrol.config.HooksConfig;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.plugin.Plugin;

public final class LuckPermsHook extends AbstractHook {

    public LuckPermsHook(HooksConfig hooksConfig) {
        super("luckperms", "LuckPerms", "LuckPerms",
                "Enables accurate permission source/group lookups in the permission debugger.", hooksConfig);
    }

    @Override
    protected String extraNotes(Plugin plugin) {
        try {
            LuckPermsProvider.get();
            return super.extraNotes(plugin);
        } catch (IllegalStateException e) {
            return "Plugin present but API not yet registered.";
        }
    }
}
