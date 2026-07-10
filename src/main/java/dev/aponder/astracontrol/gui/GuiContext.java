package dev.aponder.astracontrol.gui;

import dev.aponder.astracontrol.config.ConfigManager;
import dev.aponder.astracontrol.config.GuiConfig;
import dev.aponder.astracontrol.language.LanguageManager;
import dev.aponder.astracontrol.lifecycle.ReloadManager;

/**
 * Bundles the handful of dependencies every menu needs (its own layout source, a way
 * to open other menus, and localization) so menu constructors stay short.
 */
public record GuiContext(GuiConfig guiConfig, GUIManager guiManager, LanguageManager language,
                          ConfigManager config, ReloadManager reloadManager) {
}
