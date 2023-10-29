package org.lavajuno.mirrorlog.server;

import org.lavajuno.mirrorlog.config.ApplicationConfig;
import org.lavajuno.mirrorlog.io.OutputController;
import org.lavajuno.mirrorlog.main.LogMap;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * ServerController accepts incoming connections and assigns
 * them to ServerThreads in the thread pool.
 */
public class ServerController extends Thread {
    /**
     * The socket that the server listens on
     */
    private final ServerSocket socket;

    /**
     * The thread pool that connections are assigned to
     */
    private final ExecutorService threadPool;

    /**
     * This ServerController's application configuration
     */
    final ApplicationConfig application_config;

    /**
     * This ServerController's OutputController
     */
    final OutputController output_controller;

    /**
     * Instantiates a ServerController.
     * @throws IOException if the socket cannot be created
     */
    public ServerController() throws IOException {
        application_config = new ApplicationConfig(LogMap.CONFIG_FILE_PATH);
        output_controller = new OutputController(application_config);
        threadPool = Executors.newFixedThreadPool(application_config.getThreads());
        socket = new ServerSocket(application_config.getPort());
    }

    @Override
    public void run() {
        output_controller.start();
        output_controller.submitEvent(
                "Log Server",
                0,
                "Startup complete."
        );
        while(true) {
            try {
                threadPool.submit(new ServerThread(socket.accept(), output_controller, application_config));
            } catch(IOException e) {
                if (socket.isClosed()) { return; }
                System.err.println("Failed to accept a connection. (IOException)");
            } catch(RejectedExecutionException e) {
                System.err.println("Failed to accept a connection. (Thread pool is full)");
            }
        }
    }

    @Override
    public void interrupt() {
        try {
            socket.close();
        } catch(IOException e) {
            System.err.println("Failed to close server socket. (IOException)");
        }
    }

    /**
     * Shuts down the thread pool and stops the server.
     */
    public void close() {
        System.out.println("Sending shutdown signal to thread pool...");
        threadPool.shutdownNow();
        System.out.println("Sending shutdown signal to output controller...");
        output_controller.interrupt();
        try {
            output_controller.join(LogMap.IO_SHUTDOWN_TIMEOUT);
        } catch(InterruptedException e) {
            System.err.println("Interrupted while shutting down output controller. Skipping timeout.");
        }
        if(output_controller.isAlive()) {
            System.out.println("Waiting on output controller to shut down...");
        }
        if(!threadPool.isTerminated()) {
            System.out.println("Waiting on thread pool to shut down...");
        }

    }
}
