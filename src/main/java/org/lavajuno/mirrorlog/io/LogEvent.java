package org.lavajuno.mirrorlog.io;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * LogEvent stores a single log entry and provides functionality
 * to convert it to a string to be printed to the console or stored in a file.
 */
public class LogEvent {
    /**
     * The length that component names should be padded to
     */
    private static final int NAME_PAD_LENGTH = 24;

    /**
     * The date and time format of log events.
     */
    private static final SimpleDateFormat LOG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Color and header of messages with severity 0
     */
    private static final String SEVERITY_INFO_PRETTY = " \u001B[32m[ INFO ]\u001B[0m  ";
    private static final String SEVERITY_INFO = " [ INFO ]  ";

    /**
     * Color and header of messages with severity 1
     */
    private static final String SEVERITY_WARN_PRETTY = " \u001B[33m[ WARN ]\u001B[0m  ";
    private static final String SEVERITY_WARN = " [ WARN ]  ";

    /**
     * Color and header of messages with severity 2
     */
    private static final String SEVERITY_ERROR_PRETTY = " \u001B[31m[ ERROR ]\u001B[0m ";
    private static final String SEVERITY_ERROR = " [ ERROR ] ";

    /**
     * Color and header of messages with severity 3
     */
    private static final String SEVERITY_FATAL_PRETTY = " \u001B[31m[ FATAL ]\u001B[0m ";
    private static final String SEVERITY_FATAL = " [ FATAL ] ";

    /**
     * The component name for this LogEvent
     */
    private final String component_name;

    /**
     * The message for this LogEvent
     */
    private final String message;

    /**
     * The severity for this LogEvent
     */
    private final int severity;

    /**
     * Instantiates a LogEvent.
     * @param component_name The component name to be logged
     * @param severity The severity of the event
     * @param message The message to be logged
     */
    public LogEvent(String component_name, int severity, String message) {
        this.component_name = component_name;
        this.severity = severity;
        this.message = message;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(LOG_DATE_FORMAT.format(new Date()));
        switch(severity) {
            case 1:
                sb.append(SEVERITY_WARN);
                break;
            case 2:
                sb.append(SEVERITY_ERROR);
                break;
            case 3:
                sb.append(SEVERITY_FATAL);
                break;
            default:
                sb.append(SEVERITY_INFO);
                break;
        }
        sb.append(component_name);
        sb.append(" ".repeat(Math.max(0, NAME_PAD_LENGTH - component_name.length())));
        sb.append(" : ");
        sb.append(message);
        return sb.toString();
    }

    public String toPrettyString() {
        StringBuilder sb = new StringBuilder();
        sb.append(LOG_DATE_FORMAT.format(new Date()));
        switch(severity) {
            case 1:
                sb.append(SEVERITY_WARN_PRETTY);
                break;
            case 2:
                sb.append(SEVERITY_ERROR_PRETTY);
                break;
            case 3:
                sb.append(SEVERITY_FATAL_PRETTY);
                break;
            default:
                sb.append(SEVERITY_INFO_PRETTY);
                break;
        }
        sb.append(component_name);
        sb.append(" ".repeat(Math.max(0, NAME_PAD_LENGTH - component_name.length())));
        sb.append(" : ");
        sb.append(message);
        return sb.toString();
    }
}
