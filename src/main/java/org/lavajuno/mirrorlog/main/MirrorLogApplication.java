package org.lavajuno.mirrorlog.main;

import java.io.IOException;
import java.util.Scanner;
import org.lavajuno.mirrorlog.server.ServerController;

/**
 * MirrorLogApplication handles startup and shutdown of the server.
 */
public class MirrorLogApplication {

    /**
     * Starts MirrorLog.
     * @param args Unused
     */
    public static void main(String[] args) {
        ServerController server_controller;

        System.out.println("Starting MirrorLog server...");
        try {
            server_controller = new ServerController();
            server_controller.start();
        } catch(IOException e) {
            System.err.println("Failed to start server! (IOException)");
            System.err.println(e.getMessage());
        }
    }
}
