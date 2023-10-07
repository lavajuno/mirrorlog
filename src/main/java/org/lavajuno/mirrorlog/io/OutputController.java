package org.lavajuno.mirrorlog.io;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * OutputController queues log entries from multiple ServerThreads
 * and handles printing them to the console as well as writing them to files.
 */
public class OutputController extends Thread {
    /**
     * New log entries will be stored in a blocking FIFO queue.
     */
    private final BlockingQueue<LogEvent> outputQueue;

    /**
     * Instantiates an OutputController.
     */
    public OutputController() {
        outputQueue = new LinkedBlockingQueue<>();
    }

    /**
     * Submits an event to be logged
     * @param component_name Component name of the log event
     * @param severity Severity of the log event
     * @param message Message to be logged
     */
    public void submitEvent(String component_name, int severity, String message) {
        outputQueue.add(new LogEvent(component_name, severity, message));
    }

    /**
     * OutputController's thread.
     * Takes
     */
    @Override
    public void run() {
        LogEvent entry;
        this.submitEvent(
                "Log Server",
                0,
                "Started output controller."
        );
        try {
            while(true) {
                entry = outputQueue.take();
                System.out.println(entry);
            }
        } catch(InterruptedException e) {
            System.out.println("Output controller: Stopping now.");
        }

    }
}
