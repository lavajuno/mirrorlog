package org.lavajuno.mirrorlog.server;

import org.lavajuno.mirrorlog.config.ApplicationConfig;
import org.lavajuno.mirrorlog.io.OutputController;

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
     * Path to the program configuration file
     */
    private static final String CONFIG_FILE_PATH = "mirrorlog.conf.yml";

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
    final OutputController outputController;


    /**
     * Instantiates a ServerController.
     * @param port The port to open the server on
     * @throws IOException if the socket cannot be created
     */
    public ServerController(int port) throws IOException {
        application_config = new ApplicationConfig(CONFIG_FILE_PATH);
        outputController = new OutputController(application_config);
        threadPool = Executors.newFixedThreadPool(application_config.getThreads());
        socket = new ServerSocket(port);
    }

    /**
     * Instantiates a ServerController.
     * @throws IOException if the socket cannot be created
     */
    public ServerController() throws IOException {
        application_config = new ApplicationConfig(CONFIG_FILE_PATH);
        outputController = new OutputController(application_config);
        threadPool = Executors.newFixedThreadPool(application_config.getThreads());
        socket = new ServerSocket(application_config.getPort());
    }

    @Override
    public void run() {
        outputController.start();
        outputController.submitEvent(
                "Log Server",
                0,
                "Started server."
        );

        while(true) {
            try {
                threadPool.submit(new ServerThread(socket.accept(), outputController));
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
     * It first tries to do this gracefully, but if it cannot, it will force them to stop.
     */
    public void close() {
        System.out.println("Sending shutdown signal to thread pool...");
        threadPool.shutdownNow();
        System.out.println("Sending shutdown signal to output controller...");
        outputController.interrupt();
        try {
            outputController.join(1000);
        } catch(InterruptedException e) {
            System.err.println("Interrupted while shutting down. Skipping timeout.");
        }
        if(outputController.isAlive()) {
            System.out.println("Waiting on output controller to shut down...");
        }
        if(!threadPool.isTerminated()) {
            System.out.println("Waiting on thread pool to shut down...");
        }

    }
}
