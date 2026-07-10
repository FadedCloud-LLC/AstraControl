package dev.aponder.astracontrol.commands.subcommands;

import dev.aponder.astracontrol.commands.BaseCommand;
import dev.aponder.astracontrol.commands.CommandContext;
import dev.aponder.astracontrol.language.LanguageManager;
import dev.aponder.astracontrol.language.TranslationKey;
import dev.aponder.astracontrol.plugins.PluginDependencyAnalyzer;
import dev.aponder.astracontrol.plugins.PluginInfo;
import dev.aponder.astracontrol.plugins.PluginInspector;
import dev.aponder.astracontrol.text.MessageUtil;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Backs {@code /astractrl plugins} (list), {@code /astractrl plugin <plugin>}
 * (detail), and {@code /astractrl dependencies <plugin>} (dependency status).
 */
public final class PluginsCommand extends BaseCommand {

    public enum Mode {
        LIST, DETAIL, DEPENDENCIES
    }

    private final PluginInspector inspector;
    private final PluginDependencyAnalyzer analyzer = new PluginDependencyAnalyzer();
    private final Mode mode;
    private final String commandName;

    public PluginsCommand(LanguageManager language, PluginInspector inspector, Mode mode, String commandName) {
        super(language);
        this.inspector = inspector;
        this.mode = mode;
        this.commandName = commandName;
    }

    @Override
    public String name() {
        return commandName;
    }

    @Override
    public String permission() {
        return "astracontrol.plugins";
    }

    @Override
    public String usage() {
        return mode == Mode.LIST ? "/astractrl plugins" : "/astractrl " + commandName + " <plugin>";
    }

    @Override
    public String description() {
        return switch (mode) {
            case LIST -> "Lists every installed plugin.";
            case DETAIL -> "Shows detailed info about a plugin.";
            case DEPENDENCIES -> "Shows a plugin's dependency status.";
        };
    }

    @Override
    public List<String> tabComplete(CommandContext ctx) {
        if (mode != Mode.LIST && ctx.argCount() <= 1) {
            String prefix = ctx.argCount() == 1 ? ctx.arg(0).toLowerCase() : "";
            return java.util.Arrays.stream(Bukkit.getPluginManager().getPlugins())
                    .map(p -> p.getDescription().getName())
                    .filter(n -> n.toLowerCase().startsWith(prefix))
                    .toList();
        }
        return List.of();
    }

    @Override
    public void execute(CommandContext ctx) {
        if (mode == Mode.LIST) {
            List<PluginInfo> plugins = inspector.allPlugins();
            String list = plugins.stream()
                    .map(p -> (p.enabled() ? "<green>" : "<red>") + p.name() + " <gray>v" + p.version())
                    .collect(Collectors.joining("\n"));
            MessageUtil.send(ctx.sender(), MessageUtil.render(
                    "<gray>[<gold>AstraControl</gold><gray>] <white>{count} plugin(s) installed:\n{list}",
                    Map.of("count", String.valueOf(plugins.size()), "list", list)));
            return;
        }

        if (ctx.argCount() < 1) {
            invalidUsage(ctx);
            return;
        }

        Optional<PluginInfo> found = inspector.find(ctx.arg(0));
        if (found.isEmpty()) {
            language.send(ctx.sender(), TranslationKey.PLUGIN_NOT_FOUND, Map.of("plugin", ctx.arg(0)));
            return;
        }
        PluginInfo info = found.get();

        if (mode == Mode.DETAIL) {
            MessageUtil.send(ctx.sender(), MessageUtil.render(
                    "<gray>[<gold>AstraControl</gold><gray>] <white>{name} <gray>v{version}\n"
                            + "<gray>Authors: <white>{authors}\n"
                            + "<gray>Status: <white>{status}\n"
                            + "<gray>Main: <white>{main}\n"
                            + "<gray>Website: <white>{website}\n"
                            + "<gray>Description: <white>{description}",
                    Map.of(
                            "name", info.name(),
                            "version", info.version(),
                            "authors", info.authors().isEmpty() ? "Unknown" : String.join(", ", info.authors()),
                            "status", info.enabled() ? "Enabled" : "Disabled",
                            "main", info.mainClass(),
                            "website", info.website() == null ? "None" : info.website(),
                            "description", info.description() == null ? "None" : info.description()
                    )));
            return;
        }

        List<PluginDependencyAnalyzer.DependencyStatus> statuses = analyzer.analyze(info);
        String depends = statuses.stream().filter(PluginDependencyAnalyzer.DependencyStatus::required)
                .map(this::formatDependency)
                .collect(Collectors.joining("\n"));
        String softDepends = statuses.stream().filter(s -> !s.required())
                .map(this::formatDependency)
                .collect(Collectors.joining("\n"));

        MessageUtil.send(ctx.sender(), MessageUtil.render(
                "<gray>[<gold>AstraControl</gold><gray>] <white>{name} <gray>dependencies:\n"
                        + "<gray>Required: <white>{depends}\n"
                        + "<gray>Soft: <white>{soft}",
                Map.of(
                        "name", info.name(),
                        "depends", depends.isBlank() ? "None" : depends,
                        "soft", softDepends.isBlank() ? "None" : softDepends
                )));
    }

    private String formatDependency(PluginDependencyAnalyzer.DependencyStatus status) {
        String state = status.enabled() ? " <green>OK" : (status.present() ? " <yellow>DISABLED" : " <red>MISSING");
        return status.name() + state;
    }
}
