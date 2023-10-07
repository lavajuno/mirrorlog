package org.lavajuno.mirrorlog.main;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import org.lavajuno.mirrorlog.server.ServerController;
import org.lavajuno.mirrorlog.io.OutputController;

/**
 * MirrorLogApplication configures and starts ServerController.
 */
public class MirrorLogApplication {
    public static final int DEFAULT_PORT = 4357;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        ServerController serverController;

        switch(args.length) {
            case 0:
                System.out.println("Using default port.");
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

        System.out.println("Starting log server on port " + port + "...");
        try {
            serverController = new ServerController(port);
            serverController.start();
            Scanner scanner = new Scanner(System.in);
            while(true) {
                System.out.println("----- Input 't' to terminate. -----");
                if(scanner.nextLine().equalsIgnoreCase("t")) {
                    System.out.println("Shutting down server...");
                    serverController.interrupt();
                    serverController.close();
                    System.exit(0);
                }
            }
        } catch(IOException e) {
            System.err.println("Failed to start server! (IOException");
            System.err.println(Arrays.toString(e.getStackTrace()));
        }
    }
}
