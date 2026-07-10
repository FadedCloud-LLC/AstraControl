package dev.aponder.astracontrol;

import dev.aponder.astracontrol.lifecycle.PluginBootstrap;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * AstraControl entry point. All construction/wiring lives in {@link PluginBootstrap}
 * so this class stays a thin lifecycle shell.
 */
public final class AstraControlPlugin extends JavaPlugin {

    private PluginBootstrap bootstrap;

    @Override
    public void onEnable() {
        try {
            this.bootstrap = new PluginBootstrap(this);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "AstraControl failed to enable", e);
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (bootstrap != null) {
            bootstrap.shutdownHandler().shutdown();
        }
    }
}
