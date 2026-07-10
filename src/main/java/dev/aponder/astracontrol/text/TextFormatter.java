package dev.aponder.astracontrol.text;

import java.time.Duration;

/**
 * Small formatting helpers shared by the health monitor, GUI menus, and commands.
 */
public final class TextFormatter {

    private TextFormatter() {
    }

    public static String percent(double value) {
        return String.format("%.1f%%", value);
    }

    public static String decimal(double value, int places) {
        return String.format("%." + places + "f", value);
    }

    public static String bytesToMegabytes(long bytes) {
        return (bytes / (1024L * 1024L)) + " MB";
    }

    public static String duration(Duration duration) {
        long days = duration.toDaysPart();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        StringBuilder builder = new StringBuilder();
        if (days > 0) {
            builder.append(days).append("d ");
        }
        if (days > 0 || hours > 0) {
            builder.append(hours).append("h ");
        }
        if (days > 0 || hours > 0 || minutes > 0) {
            builder.append(minutes).append("m ");
        }
        builder.append(seconds).append("s");
        return builder.toString();
    }

    public static String truncate(String input, int maxLength) {
        if (input == null) {
            return "";
        }
        if (input.length() <= maxLength) {
            return input;
        }
        return input.substring(0, Math.max(0, maxLength - 1)) + "…";
    }
}
