package org.lavajuno.mirrorlog.main;

import org.lavajuno.mirrorlog.server.ServerController;
import org.lavajuno.mirrorlog.io.OutputController;

import java.io.IOException;
import java.util.Arrays;

public class MirrorLogApplication {
    public static final int DEFAULT_PORT = 4357;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        ServerController serverController;
        OutputController outputController;

        switch(args.length) {
            case 0:
                System.out.println("Using default port (" + port + ").");
                break;
            case 1:
                try {
                    port = Integer.parseInt(args[0]);
                    if(port <= 1024 | port >= 65535) {
                        System.out.println("Invalid port. (Must be > 1024 and < 65535");
                        return;
                    }
                } catch(NumberFormatException e) {
                    System.out.println("Usage: java -jar mirrorlog.jar {port}");
                    return;
                }
                break;
            default:
                System.out.println("Usage: java -jar mirrorlog.jar {port}");
                return;
        }

        System.out.println("Starting log server on port " + port + ".");
        try {
            serverController = new ServerController(port);
            serverController.start();
        } catch(IOException e) {
            System.err.println("Failed to start server! (IOException");
            System.err.println(Arrays.toString(e.getStackTrace()));
        }

    }
}
