package org.lavajuno.mirrorlog.io;

import java.io.IOException;
import java.util.concurrent.SynchronousQueue;

public class OutputController extends Thread {
    private SynchronousQueue<LogEntry> outputQueue;

    public OutputController() {
        outputQueue = new SynchronousQueue<>();
    }

    public void submitEntry(String component_name, int severity, String message) throws IOException {
        try {
            outputQueue.put(new LogEntry(component_name, severity, message));
        } catch(InterruptedException e) {
            throw new IOException("Failed to submit entry.");
        }
    }

    @Override
    public void run() {
        LogEntry entry;
        System.out.println("OutputController: Started.");
        try {
            while(true) {
                entry = outputQueue.take();
                System.out.println(entry);
            }
        } catch(InterruptedException e) {
            System.err.println("OutputController: Interrupted!");
        }


    }
}
