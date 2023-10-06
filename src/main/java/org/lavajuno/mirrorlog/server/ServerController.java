package org.lavajuno.mirrorlog.server;

import org.lavajuno.mirrorlog.io.LogEntry;
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
    private static final int THREAD_POOL_SIZE = 16;

    private final ServerSocket socket;
    private final ExecutorService threadPool;
    final OutputController outputController;

    public ServerController(int port) throws IllegalArgumentException, IOException {
        if(port < 1024 || port > 65535) {
            throw new IllegalArgumentException("ServerController: Invalid port.");
        }
        outputController = new OutputController();
        threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        socket = new ServerSocket(port);
    }

    @Override
    public void run() {
        outputController.start();
        System.out.println("ServerController: Started.");

        while(true) {
            try {
                threadPool.submit(new ServerThread(socket.accept(), outputController));
            } catch(IOException e) {
                System.err.println("Failed to accept connection (IOException)");
            } catch(RejectedExecutionException e) {
                System.err.println("Failed to accept connection (Thread pool full)");
            }
        }
    }

    public void close() {
        threadPool.shutdown();
        try {
            if(!threadPool.awaitTermination(30, TimeUnit.SECONDS)) {
                System.err.println("Graceful shutdown is taking too long. Forcefully stopping sever threads.");
                threadPool.shutdownNow();
            }
        } catch(InterruptedException e) {
            System.err.println("Interrupted during graceful shutdown. Shutting down now.");
            threadPool.shutdownNow();
        }
    }

}
