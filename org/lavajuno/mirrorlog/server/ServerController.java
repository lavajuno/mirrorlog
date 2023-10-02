package org.lavajuno.mirrorlog.server;

import org.lavajuno.mirrorlog.io.OutputController;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ServerController accepts incoming connections and assigns
 * them to ServerThreads in the thread pool.
 */
public class ServerController extends Thread {
    final int THREAD_POOL_SIZE = 128;

    final ServerSocket socket;
    final ExecutorService threadPool;
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
        while(true) {
            try {
                threadPool.submit(new ServerThread(socket.accept(), outputController));
            }
        }
    }

}
