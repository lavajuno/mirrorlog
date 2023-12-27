package org.lavajuno.mirrorlog.config;

import org.lavajuno.mirrorlog.yaml.YamlElementOld;
import org.lavajuno.mirrorlog.yaml.YamlValueOld;

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
        final YamlElementOld config_root = new YamlElementOld(readLinesFromFile(config_file_path));

        /* Get configuration revision */
        final YamlValueOld config_revision = (YamlValueOld) config_root.getElement("revision");

        /* Get server configuration */
        final YamlElementOld config_server = config_root.getElement("server");
        final YamlValueOld config_threads = (YamlValueOld) config_server.getElement("threads");
        final YamlValueOld config_port = (YamlValueOld) config_server.getElement("port");
        final YamlValueOld config_timeout = (YamlValueOld) config_server.getElement("timeout");
        final YamlValueOld config_restricted = (YamlValueOld) config_server.getElement("restricted");
        final YamlElementOld config_addresses = config_server.getElement("allowed_addresses");

        /* Get log file configuration */
        final YamlElementOld config_output = config_root.getElement("output");
        final YamlValueOld config_component_pad = (YamlValueOld) config_output.getElement("component_pad");
        final YamlValueOld config_log_to_file = (YamlValueOld) config_output.getElement("log_to_file");
        final YamlValueOld config_file_duration = (YamlValueOld) config_output.getElement("file_duration");
        final YamlValueOld config_file_history = (YamlValueOld) config_output.getElement("file_history");

        /* Read in and set configuration values */
        try {
            this.revision = Integer.parseInt(config_revision.getContents());
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"version\".");
            throw new IOException("Illegal value for key \"version\".");
        }

        try {
            this.threads = Integer.parseInt(config_threads.getContents());
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"threads\".");
            throw new IOException("Illegal value for key \"threads\".");
        }

        try {
            this.port = Integer.parseInt(config_port.getContents());
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"port\".");
            throw new IOException("Illegal value for key \"port\".");
        }

        try {
            this.timeout = Integer.parseInt(config_timeout.getContents());
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"timeout\".");
            throw new IOException("Illegal value for key \"timeout\".");
        }

        this.restricted = Boolean.parseBoolean(config_restricted.getContents());

        allowed_addresses = new Vector<>();
        try {
            for(YamlElementOld i : config_addresses.getElements()) {
                allowed_addresses.add(InetAddress.getByName( ((YamlValueOld) i).getContents()) );
            }
        } catch(UnknownHostException e) {
            System.err.println("Illegal value for key \"allowed_addresses\".");
            throw new IOException("Illegal value for key \"allowed_addresses\".");
        }

        try {
            this.component_pad = Integer.parseInt(config_component_pad.getContents());
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"component_pad\".");
            throw new IOException("Illegal value for key \"component_pad\".");
        }

        this.log_to_file = Boolean.parseBoolean(config_log_to_file.getContents());

        try {
            this.file_duration = Integer.parseInt(config_file_duration.getContents());
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"file_duration\".");
            throw new IOException("Illegal value for key \"file_duration\".");
        }

        try {
            this.file_history = Integer.parseInt(config_file_history.getContents());
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
