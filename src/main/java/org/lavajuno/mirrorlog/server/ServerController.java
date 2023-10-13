package org.lavajuno.mirrorlog.server;

import org.lavajuno.mirrorlog.config.ApplicationConfig;
import org.lavajuno.mirrorlog.io.OutputController;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * ServerController accepts incoming connections and assigns
 * them to ServerThreads in the thread pool.
 */
public class ServerController extends Thread {
    /**
     * The number of simultaneous connections the server will handle before refusing additional ones
     */
    private static final int THREAD_POOL_SIZE = 32;

    /**
     * The socket that the server listens on
     */
    private final ServerSocket socket;

    /**
     * The thread pool that connections are assigned to
     */
    private final ExecutorService threadPool;

    /**
     * This ServerController's OutputController
     */
    final OutputController outputController;

    final ApplicationConfig applicationConfig;

    /**
     * Instantiates a ServerController.
     * @param port The port to open the server on
     * @throws IllegalArgumentException if the port is invalid
     * @throws IOException if the socket cannot be created
     */
    public ServerController(int port) throws IllegalArgumentException, IOException {
        if(port < 1024 || port > 65535) {
            throw new IllegalArgumentException("ServerController: Invalid port.");
        }
        outputController = new OutputController();
        threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        socket = new ServerSocket(port);
        applicationConfig = new ApplicationConfig();
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
                outputController.submitEvent(
                        "Log Server",
                        2,
                        "Failed to accept a connection. (IOException)"
                );
            } catch(RejectedExecutionException e) {
                outputController.submitEvent(
                        "Log Server",
                        1,
                        "Failed to accept a connection. (Thread pool is full!)"
                );
            }
        }
    }

    /**
     * Shuts down the thread pool and stops the server.
     * It first tries to do this gracefully, but if it cannot, it will force them to stop.
     */
    public void close() {
        System.out.println("Shutting down thread pool...");
        threadPool.shutdown();
        try {
            if(!threadPool.awaitTermination(3, TimeUnit.SECONDS)) {
                System.err.println("Thread pool still has active connections. Shutting it down now.");
                threadPool.shutdownNow();
            }
        } catch(InterruptedException e) {
            System.err.println("Interrupted while shutting down thread pool. Stopping now.");
            threadPool.shutdownNow();
        }
        outputController.interrupt();
    }
}
