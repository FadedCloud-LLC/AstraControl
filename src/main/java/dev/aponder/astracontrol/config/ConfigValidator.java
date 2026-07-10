package dev.aponder.astracontrol.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Sanity-checks {@code config.yml} values after every load/reload so a malformed
 * config produces clear warnings instead of a silent misconfiguration or a crash.
 */
public final class ConfigValidator {

    public List<String> validate(FileConfiguration config) {
        List<String> problems = new ArrayList<>();

        checkRange(config, "health.warnings.tps-threshold", 0.0, 20.0, problems);
        checkRange(config, "health.warnings.mspt-threshold", 0.0, 1000.0, problems);
        checkRange(config, "health.warnings.memory-usage-threshold-percent", 1.0, 100.0, problems);
        checkPositive(config, "health.update-interval-ticks", problems);
        checkPositive(config, "error-watcher.buffer-size", problems);

        String language = config.getString("language", "en");
        if (language == null || language.isBlank()) {
            problems.add("language must not be empty; falling back to 'en'");
        }

        return problems;
    }

    public void validateAndReport(FileConfiguration config, Consumer<String> warningSink) {
        for (String problem : validate(config)) {
            warningSink.accept(problem);
        }
    }

    private void checkRange(FileConfiguration config, String path, double min, double max, List<String> problems) {
        if (!config.isSet(path)) {
            return;
        }
        double value = config.getDouble(path);
        if (value < min || value > max) {
            problems.add(path + " (" + value + ") should be between " + min + " and " + max);
        }
    }

    private void checkPositive(FileConfiguration config, String path, List<String> problems) {
        if (!config.isSet(path)) {
            return;
        }
        if (config.getInt(path) <= 0) {
            problems.add(path + " must be greater than zero");
        }
    }
}
