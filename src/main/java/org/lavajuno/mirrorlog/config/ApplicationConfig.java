package org.lavajuno.mirrorlog.config;

import org.lavajuno.mirrorlog.yaml.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;

/**
 * ApplicationConfig loads and stores the program configuration, and
 * provides functionality for retrieving configuration values.
 */
@SuppressWarnings("unused")
public class ApplicationConfig {
    private final int threads;
    private final int port;
    private final int timeout;
    private final int revision;
    private final boolean restricted;
    private final Vector<InetAddress> allowed_addresses;
    private final int component_pad;
    private final boolean log_to_file;
    private final int file_duration;
    private final int file_history;

    /**
     * Instantiates an ApplicationConfig.
     * Loads program configuration from the specified configuration file,
     * and stores the resulting configuration in the instance.
     * @param config_file_path Path to the configuration file to load
     * @throws IOException If loading the configuration fails.
     */
    public ApplicationConfig(String config_file_path) throws IOException {
        /* Parse configuration file */
        final YamlMap config_root = new YamlMap(readLinesFromFile(config_file_path));

        /* Get configuration revision */
        final YamlValue config_revision = ((YamlPair) config_root.getElement("revision")).getValue();

        /* Get server configuration */
        final YamlMap config_server = (YamlMap) config_root.getElement("server");
        final YamlValue config_threads = ((YamlPair) config_server.getElement("threads")).getValue();
        final YamlValue config_port = ((YamlPair) config_server.getElement("port")).getValue();
        final YamlValue config_timeout = ((YamlPair) config_server.getElement("timeout")).getValue();
        final YamlValue config_restricted = ((YamlPair) config_server.getElement("restricted")).getValue();
        final YamlList config_addresses = (YamlList) config_server.getElement("allowed_addresses");

        /* Get log file configuration */
        final YamlMap config_output = (YamlMap) config_root.getElement("output");
        final YamlValue config_component_pad = ((YamlPair) config_output.getElement("component_pad")).getValue();
        final YamlValue config_log_to_file = ((YamlPair) config_output.getElement("log_to_file")).getValue();
        final YamlValue config_file_duration = ((YamlPair) config_output.getElement("file_duration")).getValue();
        final YamlValue config_file_history = ((YamlPair) config_output.getElement("file_history")).getValue();

        /* Read in and set configuration values */
        try {
            this.revision = config_revision.toInt();
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"version\".");
            throw new IOException("Illegal value for key \"version\".");
        }

        try {
            this.threads = config_threads.toInt();
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"threads\".");
            throw new IOException("Illegal value for key \"threads\".");
        }

        try {
            this.port = config_port.toInt();
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"port\".");
            throw new IOException("Illegal value for key \"port\".");
        }

        try {
            this.timeout = config_timeout.toInt();
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"timeout\".");
            throw new IOException("Illegal value for key \"timeout\".");
        }

        this.restricted = Boolean.parseBoolean(config_restricted.toString());

        allowed_addresses = new Vector<>();
        try {
            for(YamlElement i : config_addresses.getElements()) {
                allowed_addresses.add(InetAddress.getByName( i.toString()) );
            }
        } catch(UnknownHostException e) {
            System.err.println("Illegal value for key \"allowed_addresses\".");
            throw new IOException("Illegal value for key \"allowed_addresses\".");
        }

        try {
            this.component_pad = config_component_pad.toInt();
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"component_pad\".");
            throw new IOException("Illegal value for key \"component_pad\".");
        }

        this.log_to_file = Boolean.parseBoolean(config_log_to_file.toString());

        try {
            this.file_duration = config_file_duration.toInt();
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"file_duration\".");
            throw new IOException("Illegal value for key \"file_duration\".");
        }

        try {
            this.file_history = config_file_history.toInt();
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"file_history\".");
            throw new IOException("Illegal value for key \"file_history\".");
        }

        /* Finished loading configuration */
        System.out.println("Application configuration created:\n" + this);
    }

    /**
     * The number of simultaneous connections the server will handle
     * @return The value of "threads"
     */
    public int getThreads() { return threads; }

    /**
     * The port the server listens on
     * @return The value of "port"
     */
    public int getPort() { return port; }

    /**
     * The socket timeout for inactive clients
     * @return The value of "timeout"
     */
    public int getTimeout() { return timeout; }

    /**
     * Whether access is restricted to certain IP addresses
     * @return The value of "restricted"
     */
    public boolean isRestricted() { return restricted; }

    /**
     * The list of allowed IP addresses
     * @return The value of "allowed_addresses"
     */
    public Vector<InetAddress> getAllowedAddresses() { return allowed_addresses; }

    /**
     * The length that component names should be padded to
     * @return The value of "component_pad"
     */
    public int getComponentPad() { return component_pad; }

    /**
     * Whether the program should log to files as well as the console
     * @return The value of "log_to_file"
     */
    public boolean getLogToFile() { return log_to_file; }

    /**
     * The amount of time to be logged in each file
     * @return The value of "file_duration"
     */
    public int getFileDuration() { return file_duration; }

    /**
     * The number of old log files to keep
     * @return The value of "file_history"
     */
    public int getFileHistory() { return file_history; }

    /**
     * Reads a list of lines from a file
     * @param file_path File path to read
     * @return Lines in the file
     * @throws IOException If reading from the file fails
     */
    private static List<String> readLinesFromFile(String file_path) throws IOException {
        try {
            BufferedReader f = new BufferedReader(new FileReader(file_path));
            Vector<String> lines = new Vector<>();
            for(String line = f.readLine(); line != null; line = f.readLine()) {
                lines.add(line);
            }
            f.close();
            return lines;
        } catch(FileNotFoundException e) {
            System.err.println("File \"" + file_path + "\" could not be read. (Not Found)");
            throw new IOException("File \"" + file_path + "\" could not be read. (Not Found)");
        } catch(IOException e) {
            System.err.println("File \"" + file_path + "\" could not be read. (IOException)");
            throw(e);
        }
    }

    @Override
    public String toString() {
        return String.format(
            """
            {
                Revision: %s
                Threads: %s
                Port: %s
                Timeout: %s
                Restricted: %s
                Allowed addresses: %s
                Component padding: %s
                Log to file: %s
                File duration: %s
                File history: %s
            }""",
            revision,
            threads,
            port,
            timeout,
            restricted,
            allowed_addresses,
            component_pad,
            log_to_file,
            file_duration,
            file_history
        );
    }
}
