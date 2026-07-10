package dev.aponder.astracontrol.placeholder;

import java.util.Map;

/**
 * Every representation of a single resolved placeholder (or block of text), ready to
 * display in the Placeholder Tester GUI or {@code /astractrl papi} output.
 */
public record PlaceholderResult(
        String input,
        boolean placeholderApiAvailable,
        boolean resolved,
        String raw,
        String plain,
        String miniMessage,
        String legacy
) {

    public static PlaceholderResult unavailable(String input) {
        return new PlaceholderResult(input, false, false, null, null, null, null);
    }

    public static PlaceholderResult unresolved(String input) {
        return new PlaceholderResult(input, true, false, null, null, null, null);
    }

    public Map<String, String> asDisplayMap() {
        return Map.of(
                "input", input,
                "raw", raw == null ? "" : raw,
                "plain", plain == null ? "" : plain,
                "minimessage", miniMessage == null ? "" : miniMessage,
                "legacy", legacy == null ? "" : legacy
        );
    }

    public String forMode(PlaceholderRenderMode mode) {
        return switch (mode) {
            case RAW -> raw;
            case PLAIN -> plain;
            case MINIMESSAGE -> miniMessage;
            case LEGACY -> legacy;
        };
    }
}
