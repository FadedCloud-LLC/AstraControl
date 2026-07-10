package dev.aponder.astracontrol.language;

/**
 * Resolves a {@link TranslationKey} to its raw MiniMessage template for a given
 * language, falling back to the key's hard-coded English default when the active
 * language file is missing the key.
 */
public final class LanguageResolver {

    private final LanguageRegistry registry;
    private final TranslationCache cache;
    private volatile String activeLanguageCode = "en";

    public LanguageResolver(LanguageRegistry registry, TranslationCache cache) {
        this.registry = registry;
        this.cache = cache;
    }

    public void setActiveLanguage(String code) {
        cache.invalidate();
        this.activeLanguageCode = code;
    }

    public String activeLanguage() {
        return activeLanguageCode;
    }

    public String resolve(TranslationKey key) {
        return cache.getOrCompute(key, () -> {
            LanguageFile file = registry.get(activeLanguageCode);
            if (file.has(key.path())) {
                return file.get(key.path());
            }
            return key.defaultValue();
        });
    }
}
