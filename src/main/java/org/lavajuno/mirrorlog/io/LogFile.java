package org.lavajuno.mirrorlog.io;

import org.lavajuno.mirrorlog.config.ApplicationConfig;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogFile {
    private static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmm");

    private final long DATE_EXPIRY;

    private final FileWriter log_file_writer;
    private final PrintWriter log_print_writer;

    public LogFile(ApplicationConfig application_config) throws IOException {
        Files.createDirectories(Paths.get("logs/"));
        final String DATE_TAG = FILE_DATE_FORMAT.format(new Date());
        final String FILE_PATH = "logs/" + DATE_TAG + ".log";
        DATE_EXPIRY = Long.parseLong(DATE_TAG)+ application_config.getFileDuration();

        log_file_writer = new FileWriter(FILE_PATH, StandardCharsets.UTF_8, true);
        log_print_writer = new PrintWriter(log_file_writer, true);
    }

    public void print(LogEvent event) {
        log_print_writer.println(event);
    }

    public void close() {
        try {
            log_print_writer.flush();
            log_file_writer.flush();
            log_print_writer.close();
            log_file_writer.close();
        } catch(IOException e) {
            System.err.println("Failed to close log file writer.");
        }
    }

    public boolean isExpired() {
        return DATE_EXPIRY < Long.parseLong(FILE_DATE_FORMAT.format(new Date()));
    }
}
