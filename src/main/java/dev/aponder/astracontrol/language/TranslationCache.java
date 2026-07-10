package dev.aponder.astracontrol.language;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Caches the raw (unrendered) MiniMessage template for each resolved
 * {@link TranslationKey} so repeated lookups skip the YAML access. Invalidated
 * whenever the active language is reloaded or switched.
 */
public final class TranslationCache {

    private final Map<TranslationKey, String> cache = new ConcurrentHashMap<>();

    public String getOrCompute(TranslationKey key, java.util.function.Supplier<String> supplier) {
        return cache.computeIfAbsent(key, k -> supplier.get());
    }

    public void invalidate() {
        cache.clear();
    }
}
