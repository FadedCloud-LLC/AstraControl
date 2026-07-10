package dev.aponder.astracontrol.language;

import java.util.ArrayList;
import java.util.List;

/**
 * Confirms that the active language file defines every known {@link TranslationKey}.
 * Missing keys are not fatal - {@link LanguageResolver} silently falls back to the
 * built-in English default - but they are worth surfacing to the server owner.
 */
public final class LanguageValidator {

    public List<String> findMissingKeys(LanguageFile file) {
        List<String> missing = new ArrayList<>();
        for (TranslationKey key : TranslationKey.values()) {
            if (!file.has(key.path())) {
                missing.add(key.path());
            }
        }
        return missing;
    }
}
