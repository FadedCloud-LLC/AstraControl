package dev.aponder.astracontrol.lifecycle;

import dev.aponder.astracontrol.broadcast.BroadcastManager;
import dev.aponder.astracontrol.commands.CommandCooldownManager;
import dev.aponder.astracontrol.commands.CommandManager;
import dev.aponder.astracontrol.commands.PluginCommandExecutor;
import dev.aponder.astracontrol.commands.PluginTabCompleter;
import dev.aponder.astracontrol.commands.subcommands.BroadcastCommand;
import dev.aponder.astracontrol.commands.subcommands.DebugCommand;
import dev.aponder.astracontrol.commands.subcommands.ErrorsCommand;
import dev.aponder.astracontrol.commands.subcommands.GuiCommand;
import dev.aponder.astracontrol.commands.subcommands.HealthCommand;
import dev.aponder.astracontrol.commands.subcommands.HelpCommand;
import dev.aponder.astracontrol.commands.subcommands.HooksCommand;
import dev.aponder.astracontrol.commands.subcommands.MaintenanceCommand;
import dev.aponder.astracontrol.commands.subcommands.PlaceholderCommand;
import dev.aponder.astracontrol.commands.subcommands.PlayerCommand;
import dev.aponder.astracontrol.commands.subcommands.PluginsCommand;
import dev.aponder.astracontrol.commands.subcommands.ReloadCommand;
import dev.aponder.astracontrol.commands.subcommands.StatusCommand;
import dev.aponder.astracontrol.config.ConfigManager;
import dev.aponder.astracontrol.errors.ErrorBuffer;
import dev.aponder.astracontrol.errors.ErrorExporter;
import dev.aponder.astracontrol.errors.ErrorWatcher;
import dev.aponder.astracontrol.gui.GUIManager;
import dev.aponder.astracontrol.gui.GuiContext;
import dev.aponder.astracontrol.gui.MenuManager;
import dev.aponder.astracontrol.gui.MenuRegistry;
import dev.aponder.astracontrol.gui.MenuSessionManager;
import dev.aponder.astracontrol.gui.menus.BroadcastMenu;
import dev.aponder.astracontrol.gui.menus.ErrorWatcherMenu;
import dev.aponder.astracontrol.gui.menus.HealthMenu;
import dev.aponder.astracontrol.gui.menus.HookStatusMenu;
import dev.aponder.astracontrol.gui.menus.MainControlMenu;
import dev.aponder.astracontrol.gui.menus.MaintenanceMenu;
import dev.aponder.astracontrol.gui.menus.PermissionDebugMenu;
import dev.aponder.astracontrol.gui.menus.PlaceholderTesterMenu;
import dev.aponder.astracontrol.gui.menus.PlayerListMenu;
import dev.aponder.astracontrol.gui.menus.PluginListMenu;
import dev.aponder.astracontrol.health.PlatformDetector;
import dev.aponder.astracontrol.health.ServerHealthManager;
import dev.aponder.astracontrol.hooks.HookManager;
import dev.aponder.astracontrol.hooks.HookStatus;
import dev.aponder.astracontrol.language.LanguageManager;
import dev.aponder.astracontrol.language.TranslationKey;
import dev.aponder.astracontrol.logging.DebugLogger;
import dev.aponder.astracontrol.logging.HookStatusRegistry;
import dev.aponder.astracontrol.logging.LibraryLogSilencer;
import dev.aponder.astracontrol.logging.PluginLogger;
import dev.aponder.astracontrol.logging.StartupSummaryLogger;
import dev.aponder.astracontrol.maintenance.MaintenanceListener;
import dev.aponder.astracontrol.maintenance.MaintenanceManager;
import dev.aponder.astracontrol.metrics.MetricsManager;
import dev.aponder.astracontrol.permissions.PermissionDebugManager;
import dev.aponder.astracontrol.placeholder.PlaceholderTester;
import dev.aponder.astracontrol.players.PlayerActionService;
import dev.aponder.astracontrol.players.PlayerToolsManager;
import dev.aponder.astracontrol.plugins.PluginInspector;
import dev.aponder.astracontrol.plugins.PluginReloadRegistry;
import dev.aponder.astracontrol.scheduler.SchedulerAdapter;
import dev.aponder.astracontrol.scheduler.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

/**
 * Composition root: constructs every AstraControl subsystem in dependency order and
 * wires them together. Kept separate from {@code AstraControlPlugin} so the main
 * class stays a thin {@link JavaPlugin} entry point.
 */
public final class PluginBootstrap {

    private final JavaPlugin plugin;
    private final PluginLogger logger;
    private final SchedulerAdapter scheduler;
    private final ConfigManager configManager;
    private final DebugLogger debugLogger;
    private final LanguageManager languageManager;
    private final HookStatusRegistry hookStatusRegistry;
    private final HookManager hookManager;
    private final ServerHealthManager healthManager;
    private final ErrorBuffer errorBuffer;
    private final ErrorWatcher errorWatcher;
    private final MaintenanceManager maintenanceManager;
    private final MenuSessionManager menuSessionManager;
    private final ReloadManager reloadManager;
    private final PluginShutdownHandler shutdownHandler;

    public PluginBootstrap(JavaPlugin plugin) {
        this.plugin = plugin;

        this.logger = new PluginLogger(plugin.getLogger());
        this.scheduler = SchedulerUtil.create(plugin);
        this.configManager = new ConfigManager(plugin);
        this.debugLogger = new DebugLogger(logger, configManager::isDebug);
        LibraryLogSilencer.apply();

        this.languageManager = new LanguageManager(plugin);
        languageManager.reload(configManager.getLanguage(), debugLogger::debug);

        this.hookStatusRegistry = new HookStatusRegistry();
        this.hookManager = new HookManager(configManager.hooks(), hookStatusRegistry, debugLogger);
        hookManager.detectAll();

        this.healthManager = new ServerHealthManager(configManager, scheduler);

        PluginInspector pluginInspector = new PluginInspector();
        PluginReloadRegistry reloadRegistry = new PluginReloadRegistry();
        populateReloadRegistry(reloadRegistry);

        this.errorBuffer = new ErrorBuffer(configManager.getErrorBufferSize());
        ErrorExporter errorExporter = new ErrorExporter();
        this.errorWatcher = new ErrorWatcher(errorBuffer, record -> onNewError(record.source(), record.message()));
        if (configManager.isErrorWatcherEnabled() && configManager.isCapturePluginLogs()) {
            errorWatcher.attachToRootLogger();
        }

        this.maintenanceManager = new MaintenanceManager(plugin, configManager);
        Bukkit.getPluginManager().registerEvents(
                new MaintenanceListener(maintenanceManager, configManager, languageManager), plugin);

        BroadcastManager broadcastManager = new BroadcastManager(configManager, scheduler);
        PlaceholderTester placeholderTester = new PlaceholderTester();
        PermissionDebugManager permissionDebugManager = new PermissionDebugManager(configManager, debugLogger);
        PlayerToolsManager playerToolsManager = new PlayerToolsManager();
        PlayerActionService playerActionService = new PlayerActionService(configManager, scheduler);

        this.reloadManager = new ReloadManager(logger);
        reloadManager.addStep(() -> configManager.reload(debugLogger::debug));
        reloadManager.addStep(() -> languageManager.reload(configManager.getLanguage(), debugLogger::debug));
        reloadManager.addStep(hookManager::detectAll);
        reloadManager.addStep(() -> errorBuffer.setMaxSize(configManager.getErrorBufferSize()));
        reloadManager.addStep(() -> populateReloadRegistry(reloadRegistry));
        reloadManager.addStep(() -> refreshErrorWatcher(errorWatcher));

        this.menuSessionManager = new MenuSessionManager();
        MenuRegistry menuRegistry = new MenuRegistry();
        GUIManager guiManager = new GUIManager(menuRegistry, menuSessionManager);
        Bukkit.getPluginManager().registerEvents(new MenuManager(menuSessionManager, scheduler), plugin);

        GuiContext guiContext = new GuiContext(configManager.gui(), guiManager, languageManager, configManager, reloadManager);

        registerMenus(menuRegistry, guiContext, pluginInspector, reloadRegistry, hookStatusRegistry,
                playerToolsManager, playerActionService, permissionDebugManager, placeholderTester,
                errorExporter, maintenanceManager, broadcastManager);

        CommandCooldownManager cooldownManager = new CommandCooldownManager();
        CommandManager commandManager = new CommandManager(languageManager, cooldownManager, logger);
        registerCommands(commandManager, guiManager, guiContext, pluginInspector, hookStatusRegistry,
                maintenanceManager, broadcastManager, placeholderTester, permissionDebugManager,
                playerToolsManager, playerActionService, errorExporter);
        bindCommandExecutor(commandManager);

        if (configManager.isMetricsEnabled()) {
            new MetricsManager(plugin).start();
        }

        healthManager.start(this::onHealthWarning);

        this.shutdownHandler = new PluginShutdownHandler(scheduler, menuSessionManager, errorWatcher, healthManager);

        new StartupSummaryLogger(logger).printEnabled(
                plugin.getDescription().getVersion(),
                PlatformDetector.displayName(),
                hookStatusRegistry.activeHooks(),
                configManager.isMetricsEnabled(),
                maintenanceManager.isEnabled());
    }

    private void populateReloadRegistry(PluginReloadRegistry registry) {
        registry.clear();
        for (HookStatus status : hookStatusRegistry.all()) {
            String command = configManager.hooks().reloadCommand(status.id());
            if (command != null) {
                registry.register(configManager.hooks().pluginName(status.id(), status.displayName()), command);
            }
        }
    }

    private void refreshErrorWatcher(ErrorWatcher watcher) {
        if (configManager.isErrorWatcherEnabled() && configManager.isCapturePluginLogs()) {
            watcher.attachToRootLogger();
        } else {
            watcher.detachFromRootLogger();
        }
    }

    private void onNewError(String source, String message) {
        if (!configManager.isErrorNotifyInGame()) {
            return;
        }
        scheduler.runNow(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission(configManager.getErrorNotifyPermission())) {
                    languageManager.send(player, TranslationKey.ERROR_NOTIFICATION, Map.of("source", source, "message", message));
                }
            }
        });
    }

    private void onHealthWarning(TranslationKey key, Map<String, String> placeholders) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(configManager.getHealthNotifyPermission())) {
                languageManager.send(player, key, placeholders);
            }
        }
    }

    private void registerMenus(MenuRegistry menuRegistry, GuiContext guiContext, PluginInspector pluginInspector,
                                PluginReloadRegistry reloadRegistry, HookStatusRegistry hookStatusRegistry,
                                PlayerToolsManager playerToolsManager, PlayerActionService playerActionService,
                                PermissionDebugManager permissionDebugManager, PlaceholderTester placeholderTester,
                                ErrorExporter errorExporter, MaintenanceManager maintenanceManager,
                                BroadcastManager broadcastManager) {
        menuRegistry.register("main", player -> new MainControlMenu(guiContext));
        menuRegistry.register("health", player -> new HealthMenu(guiContext, healthManager));
        menuRegistry.register("plugin-list", player -> new PluginListMenu(guiContext, pluginInspector, reloadRegistry));
        menuRegistry.register("hooks", player -> new HookStatusMenu(guiContext, hookStatusRegistry));
        menuRegistry.register("player-list", player -> new PlayerListMenu(
                guiContext, playerToolsManager, playerActionService, permissionDebugManager, placeholderTester));
        menuRegistry.register("errors", player -> new ErrorWatcherMenu(guiContext, errorBuffer, errorExporter, plugin, scheduler));
        menuRegistry.register("maintenance", player -> new MaintenanceMenu(guiContext, maintenanceManager, broadcastManager));
        menuRegistry.register("placeholders", player -> new PlaceholderTesterMenu(guiContext, placeholderTester, player));
        menuRegistry.register("permission-debug", player -> new PermissionDebugMenu(guiContext, permissionDebugManager));
        menuRegistry.register("broadcast", player -> new BroadcastMenu(guiContext, broadcastManager));
    }

    private void registerCommands(CommandManager commandManager, GUIManager guiManager, GuiContext guiContext,
                                   PluginInspector pluginInspector, HookStatusRegistry hookStatusRegistry,
                                   MaintenanceManager maintenanceManager, BroadcastManager broadcastManager,
                                   PlaceholderTester placeholderTester, PermissionDebugManager permissionDebugManager,
                                   PlayerToolsManager playerToolsManager, PlayerActionService playerActionService,
                                   ErrorExporter errorExporter) {
        commandManager.register(new HelpCommand(languageManager, commandManager));
        commandManager.register(new ReloadCommand(languageManager, reloadManager));
        commandManager.register(new StatusCommand(languageManager, configManager, hookStatusRegistry,
                maintenanceManager, plugin.getDescription().getVersion()));
        commandManager.register(new GuiCommand(languageManager, guiManager));

        commandManager.register(new HealthCommand(languageManager, healthManager, HealthCommand.Mode.FULL, "health"));
        commandManager.register(new HealthCommand(languageManager, healthManager, HealthCommand.Mode.TPS, "tps"));
        commandManager.register(new HealthCommand(languageManager, healthManager, HealthCommand.Mode.MEMORY, "memory"));
        commandManager.register(new HealthCommand(languageManager, healthManager, HealthCommand.Mode.WORLDS, "worlds"));
        commandManager.register(new HealthCommand(languageManager, healthManager, HealthCommand.Mode.CHUNKS, "chunks"));
        commandManager.register(new HealthCommand(languageManager, healthManager, HealthCommand.Mode.ENTITIES, "entities"));

        commandManager.register(new PluginsCommand(languageManager, pluginInspector, PluginsCommand.Mode.LIST, "plugins"));
        commandManager.register(new PluginsCommand(languageManager, pluginInspector, PluginsCommand.Mode.DETAIL, "plugin"));
        commandManager.register(new PluginsCommand(languageManager, pluginInspector, PluginsCommand.Mode.DEPENDENCIES, "dependencies"));
        commandManager.register(new HooksCommand(languageManager, hookStatusRegistry));

        commandManager.register(new ErrorsCommand(languageManager, errorBuffer, errorExporter, plugin, scheduler));
        commandManager.register(new MaintenanceCommand(languageManager, maintenanceManager, broadcastManager, configManager));

        commandManager.register(new BroadcastCommand(languageManager, broadcastManager, BroadcastCommand.Mode.CHAT, "broadcast"));
        commandManager.register(new BroadcastCommand(languageManager, broadcastManager, BroadcastCommand.Mode.TITLE, "title"));
        commandManager.register(new BroadcastCommand(languageManager, broadcastManager, BroadcastCommand.Mode.ACTIONBAR, "actionbar"));

        commandManager.register(new PlaceholderCommand(languageManager, placeholderTester));
        commandManager.register(new DebugCommand(languageManager, permissionDebugManager, scheduler));

        commandManager.register(new PlayerCommand(languageManager, playerToolsManager, playerActionService,
                permissionDebugManager, placeholderTester, guiManager, guiContext, PlayerCommand.Mode.BASIC, "player"));
        commandManager.register(new PlayerCommand(languageManager, playerToolsManager, playerActionService,
                permissionDebugManager, placeholderTester, guiManager, guiContext, PlayerCommand.Mode.DETAILED, "playerinfo"));
        commandManager.register(new PlayerCommand(languageManager, playerToolsManager, playerActionService,
                permissionDebugManager, placeholderTester, guiManager, guiContext, PlayerCommand.Mode.STAFFTOOLS, "stafftools"));
    }

    private void bindCommandExecutor(CommandManager commandManager) {
        PluginCommand command = plugin.getCommand("astractrl");
        if (command == null) {
            logger.error("Command 'astractrl' is not declared in plugin.yml; AstraControl commands will not work.");
            return;
        }
        command.setExecutor(new PluginCommandExecutor(commandManager));
        command.setTabCompleter(new PluginTabCompleter(commandManager));
    }

    public PluginShutdownHandler shutdownHandler() {
        return shutdownHandler;
    }
}
