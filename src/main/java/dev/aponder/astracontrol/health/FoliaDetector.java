package dev.aponder.astracontrol.health;

/**
 * Detects whether the server is running Folia. Used by the scheduler layer to pick a
 * safe adapter and by diagnostics to report accurate platform information.
 */
public final class FoliaDetector {

    private static final boolean FOLIA = detect();

    private FoliaDetector() {
    }

    public static boolean isFolia() {
        return FOLIA;
    }

    private static boolean detect() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
