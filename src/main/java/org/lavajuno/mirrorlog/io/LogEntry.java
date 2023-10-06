package org.lavajuno.mirrorlog.io;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogEntry {
    private static final int NAME_PAD_LENGTH = 24;
    private static final SimpleDateFormat LOG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final String SEVERITY_INFO =  " \u001B[32m[ INFO ]\u001B[0m  ";
    private static final String SEVERITY_WARN =  " \u001B[33m[ WARN ]\u001B[0m  ";
    private static final String SEVERITY_ERROR = " \u001B[31m[ ERROR ]\u001B[0m ";
    private static final String SEVERITY_FATAL = " \u001B[31m[ FATAL ]\u001B[0m ";

    private final String component_name;
    private final String message;
    private final int severity;

    public LogEntry(String component_name, int severity, String message) {
        this.component_name = component_name;
        this.severity = severity;
        this.message = message;
    }

    public LogEntry(String component_name, String message) {
        this(component_name, 0, message);
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
}
