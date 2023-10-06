package org.lavajuno.mirrorlog.io;

import java.util.concurrent.SynchronousQueue;

public class OutputController {
    private SynchronousQueue<LogEntry> outputQueue;

    public OutputController() {
        // TODO implement
    }

    public int submitEntry(String component_name, int severity, String message) {
        try {
            outputQueue.put(new LogEntry(component_name, severity, message));
            return 0;
        } catch(InterruptedException e) {
            return 1;
        }

    }
}
