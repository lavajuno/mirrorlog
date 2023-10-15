package org.lavajuno.mirrorlog.config;

import org.lavajuno.mirrorlog.yaml.YamlReader;
import org.lavajuno.mirrorlog.yaml.YamlElement;
import org.lavajuno.mirrorlog.yaml.YamlList;
import org.lavajuno.mirrorlog.yaml.YamlValue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

/**
 * ApplicationConfig loads and stores the program configuration.
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
     * Loads program configuration from the specified configuration file,
     * and stores the resulting configuration in the instance.
     * @param config_file_path Path to the configuration file to load
     * @throws IOException If loading the configuration fails.
     */
    public ApplicationConfig(String config_file_path) throws IOException {
        /* Parse configuration file */
        final YamlElement config_root = YamlReader.readFromFile(config_file_path);

        /* Get configuration revision */
        final YamlValue config_revision = (YamlValue) config_root.getElement("revision");

        /* Get server configuration */
        final YamlElement config_server = config_root.getElement("server");
        final YamlValue config_threads = (YamlValue) config_server.getElement("threads");
        final YamlValue config_port = (YamlValue) config_server.getElement("port");
        final YamlValue config_timeout = (YamlValue) config_server.getElement("timeout");
        final YamlValue config_restricted = (YamlValue) config_server.getElement("restricted");
        final YamlList config_addresses = (YamlList) config_server.getElement("allowed_addresses");

        /* Get log file configuration */
        final YamlElement config_output = config_root.getElement("output");
        final YamlValue config_component_pad = (YamlValue) config_output.getElement("component_pad");
        final YamlValue config_log_to_file = (YamlValue) config_output.getElement("log_to_file");
        final YamlValue config_file_duration = (YamlValue) config_output.getElement("file_duration");
        final YamlValue config_file_history = (YamlValue) config_output.getElement("file_history");

        /* Read in and set configuration variables */

        try {
            this.revision = Integer.parseInt(config_revision.CONTENTS);
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"version\".");
            throw new IOException("Illegal value for key \"version\".");
        }

        try {
            this.threads = Integer.parseInt(config_threads.CONTENTS);
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"threads\".");
            throw new IOException("Illegal value for key \"threads\".");
        }

        try {
            this.port = Integer.parseInt(config_port.CONTENTS);
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"port\".");
            throw new IOException("Illegal value for key \"port\".");
        }

        try {
            this.timeout = Integer.parseInt(config_timeout.CONTENTS);
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"timeout\".");
            throw new IOException("Illegal value for key \"timeout\".");
        }

        this.restricted = Boolean.parseBoolean(config_restricted.CONTENTS);

        allowed_addresses = new Vector<>();
        try {
            for(String i : config_addresses.CONTENTS) {
                allowed_addresses.add(InetAddress.getByName(i));
            }
        } catch(UnknownHostException e) {
            System.err.println("Illegal value for key \"allowed_addresses\".");
            throw new IOException("Illegal value for key \"allowed_addresses\".");
        }

        try {
            this.component_pad = Integer.parseInt(config_component_pad.CONTENTS);
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"component_pad\".");
            throw new IOException("Illegal value for key \"component_pad\".");
        }

        this.log_to_file = Boolean.parseBoolean(config_log_to_file.CONTENTS);

        try {
            this.file_duration = Integer.parseInt(config_file_duration.CONTENTS);
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"file_duration\".");
            throw new IOException("Illegal value for key \"file_duration\".");
        }

        try {
            this.file_history = Integer.parseInt(config_file_history.CONTENTS);
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"file_history\".");
            throw new IOException("Illegal value for key \"file_history\".");
        }

        /* Finished loading configuration */
        System.out.println("Application configuration created: \n" + this);
    }

    /**
     * @return The number of simultaneous connections the server will handle
     */
    public int getThreads() { return threads; }

    /**
     * @return The port the server listens on
     */
    public int getPort() { return port; }

    /**
     * @return The socket timeout for inactive clients
     */
    public int getTimeout() { return timeout; }

    /**
     * @return Whether access is restricted to certain IP addresses
     */
    public boolean isRestricted() { return restricted; }

    /**
     * @return The list of allowed IP addresses
     */
    public Vector<InetAddress> getAllowedAddresses() { return allowed_addresses; }

    /**
     * @return The length that component names should be padded to
     */
    public int getComponentPad() { return component_pad; }

    /**
     * @return Whether the program should log to files as well as the console
     */
    public boolean isLogToFile() { return log_to_file; }

    /**
     * @return The amount of time to be logged in each file
     */
    public int getFileDuration() { return file_duration; }

    /**
     * @return The number of old log files to keep
     */
    public int getFileHistory() { return file_history; }

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
