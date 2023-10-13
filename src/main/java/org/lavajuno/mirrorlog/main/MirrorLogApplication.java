package org.lavajuno.mirrorlog.main;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import org.lavajuno.mirrorlog.server.ServerController;

/**
 * MirrorLogApplication configures and starts ServerController.
 */
public class MirrorLogApplication {

    /**
     * Starts MirrorLog.
     * @param args Unused
     */
    public static void main(String[] args) {
        ServerController serverController;

        System.out.println("Starting MirrorLog server...");
        try {
            serverController = new ServerController();
            serverController.start();
            Scanner scanner = new Scanner(System.in);
            while(true) {
                System.out.println("----- Input 't' to terminate. -----");
                if(scanner.nextLine().equalsIgnoreCase("t")) {
                    System.out.println("Shutting down MirrorLog server...");
                    serverController.interrupt();
                    serverController.close();
                    try {
                        serverController.join(10000);
                    } catch(InterruptedException e) {
                        System.err.println("Interrupted whilst shutting down. Skipping timeout...");
                    }
                    System.out.println("Done.");
                    System.exit(0);
                }
            }
        } catch(IOException e) {
            System.err.println("Failed to start server! (IOException)");
            System.err.println(e.getMessage());
        }
    }
}
