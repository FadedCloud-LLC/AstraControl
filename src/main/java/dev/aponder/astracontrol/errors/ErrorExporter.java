package dev.aponder.astracontrol.errors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;

/**
 * Writes an {@link ErrorBuffer} snapshot to a plain-text file under
 * {@code plugins/AstraControl/logs/}. Callers are responsible for running this off
 * the main thread if invoked from a hot path.
 */
public final class ErrorExporter {

    public File export(File targetDirectory, List<ErrorRecord> records) throws IOException {
        if (!targetDirectory.exists() && !targetDirectory.mkdirs()) {
            throw new IOException("Could not create directory: " + targetDirectory);
        }

        File file = new File(targetDirectory, "errors-" + System.currentTimeMillis() + ".log");
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8))) {
            for (ErrorRecord record : records) {
                writer.write("[" + Instant.ofEpochMilli(record.lastSeenMillis()) + "] "
                        + "(x" + record.count() + ") "
                        + record.source() + " - " + record.level() + ": " + record.message());
                writer.newLine();
                if (record.hasStackTrace()) {
                    writer.write(record.stackTrace());
                    writer.newLine();
                }
            }
        }
        return file;
    }
}
