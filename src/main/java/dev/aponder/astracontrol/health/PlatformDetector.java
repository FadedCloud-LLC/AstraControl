package dev.aponder.astracontrol.health;

/**
 * Identifies which server software AstraControl is running on. Detection is purely
 * classpath-based (no reflection into internals) and safe on every supported
 * platform.
 */
public final class PlatformDetector {

    public enum PlatformType {
        FOLIA,
        PURPUR,
        PAPER,
        SPIGOT
    }

    private static final PlatformType TYPE = detect();

    private PlatformDetector() {
    }

    public static PlatformType type() {
        return TYPE;
    }

    public static String displayName() {
        return switch (TYPE) {
            case FOLIA -> "Folia";
            case PURPUR -> "Purpur";
            case PAPER -> "Paper";
            case SPIGOT -> "Spigot";
        };
    }

    private static PlatformType detect() {
        if (FoliaDetector.isFolia()) {
            return PlatformType.FOLIA;
        }
        if (classExists("org.purpurmc.purpur.PurpurConfig")) {
            return PlatformType.PURPUR;
        }
        if (classExists("io.papermc.paper.configuration.GlobalConfiguration")
                || classExists("com.destroystokyo.paper.PaperConfig")) {
            return PlatformType.PAPER;
        }
        return PlatformType.SPIGOT;
    }

    private static boolean classExists(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
