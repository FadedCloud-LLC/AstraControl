package dev.aponder.astracontrol.language;

import dev.aponder.astracontrol.text.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Facade for AstraControl's localization system. Every user-facing message must be
 * sent through this class rather than being hard-coded at the call site.
 */
public final class LanguageManager {

    private final LanguageRegistry registry;
    private final TranslationCache cache;
    private final LanguageResolver resolver;
    private final LanguageValidator validator;

    public LanguageManager(JavaPlugin plugin) {
        this.registry = new LanguageRegistry(plugin);
        this.cache = new TranslationCache();
        this.resolver = new LanguageResolver(registry, cache);
        this.validator = new LanguageValidator();
    }

    public void setLanguage(String code) {
        resolver.setActiveLanguage(code);
    }

    public void reload(String code, Consumer<String> missingKeyWarningSink) {
        registry.reloadAll();
        resolver.setActiveLanguage(code);
        List<String> missing = validator.findMissingKeys(registry.get(code));
        for (String path : missing) {
            missingKeyWarningSink.accept("Missing translation key '" + path + "' in language/" + code + ".yml (using built-in default)");
        }
    }

    public String raw(TranslationKey key) {
        return resolver.resolve(key);
    }

    public Component render(TranslationKey key, Map<String, String> placeholders) {
        return MessageUtil.render(resolver.resolve(key), placeholders);
    }

    public Component render(TranslationKey key) {
        return render(key, Map.of());
    }

    public Component renderPrefixed(TranslationKey key, Map<String, String> placeholders) {
        Component prefix = MessageUtil.render(resolver.resolve(TranslationKey.PREFIX), Map.of());
        return prefix.append(render(key, placeholders));
    }

    public Component renderPrefixed(TranslationKey key) {
        return renderPrefixed(key, Map.of());
    }

    public void send(CommandSender target, TranslationKey key, Map<String, String> placeholders) {
        MessageUtil.send(target, renderPrefixed(key, placeholders));
    }

    public void send(CommandSender target, TranslationKey key) {
        send(target, key, Map.of());
    }
}
