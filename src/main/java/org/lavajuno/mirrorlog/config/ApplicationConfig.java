package org.lavajuno.mirrorlog.config;

import org.lavajuno.mirrorlog.yaml.YamlReader;
import org.lavajuno.mirrorlog.yaml.YamlElement;
import org.lavajuno.mirrorlog.yaml.YamlList;
import org.lavajuno.mirrorlog.yaml.YamlValue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

@SuppressWarnings("unused")
public class ApplicationConfig {
    private final int port;
    private final int revision;
    private final boolean restricted;
    private final Vector<InetAddress> allowed_addresses;
    private final boolean log_to_file;
    private final int file_duration;
    private final int file_history;

    public ApplicationConfig(String config_file_path) throws IOException {
        final YamlElement config_root = YamlReader.readFromFile(config_file_path);

        final YamlValue config_revision = (YamlValue) config_root.getElement("revision");

        final YamlElement config_server = config_root.getElement("server");
        final YamlValue config_port = (YamlValue) config_server.getElement("port");
        final YamlValue config_restricted = (YamlValue) config_server.getElement("restricted");
        final YamlList config_addresses = (YamlList) config_server.getElement("allowed_addresses");

        final YamlElement config_output = config_root.getElement("output");
        final YamlValue config_log_to_file = (YamlValue) config_output.getElement("log_to_file");
        final YamlValue config_file_duration = (YamlValue) config_output.getElement("file_duration");
        final YamlValue config_file_history = (YamlValue) config_output.getElement("file_history");

        try {
            this.revision = Integer.parseInt(config_revision.CONTENTS);
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"version\".");
            throw new IOException("Illegal value for key \"version\".");
        }

        try {
            this.port = Integer.parseInt(config_port.CONTENTS);
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"port\".");
            throw new IOException("Illegal value for key \"port\".");
        }

        this.restricted = Boolean.parseBoolean(config_port.CONTENTS);

        allowed_addresses = new Vector<>();
        try {
            for(String i : config_addresses.CONTENTS) {
                allowed_addresses.add(InetAddress.getByName(i));
            }
        } catch(UnknownHostException e) {
            System.err.println("Illegal value for key \"allowed_addresses\".");
            throw new IOException("Illegal value for key \"allowed_addresses\".");
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
    }

    @Override
    public String toString() {
        return String.format(
            """
            ApplicationConfig {
                Revision: %s
                Port: %s
                Restricted: %s
                Allowed addresses: %s
                Log to file: %s
                File duration: %s
                File history: %s
            }
            """,
            String.valueOf(revision),
            port,
            restricted,
            allowed_addresses,
            log_to_file,
            file_duration,
            file_history
        );
    }
}
