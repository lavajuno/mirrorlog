package org.lavajuno.mirrorlog.io;

import org.lavajuno.mirrorlog.config.ApplicationConfig;

import java.io.IOException;
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

    private final ApplicationConfig applicationConfig;

    private LogFile logFile;

    /**
     * Instantiates an OutputController.
     */
    public OutputController(ApplicationConfig applicationConfig) throws IOException {
        this.applicationConfig = applicationConfig;
        outputQueue = new LinkedBlockingQueue<>();
        logFile = new LogFile(applicationConfig);

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
        LogEvent event;
        try {
            while(true) {
                event = outputQueue.take();
                if(logFile.isExpired()) {
                    logFile.close();
                    try {
                        logFile = new LogFile(applicationConfig);
                    } catch(IOException e) {
                        System.err.println("Failed to create new log file!");
                    }

                }
                System.out.println(event.toPrettyString());
                logFile.print(event);
            }
        } catch(InterruptedException e) {
            System.out.println("Flushing output buffer to log file...");
            logFile.close();
        }
    }
}
