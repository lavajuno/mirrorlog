package org.lavajuno.mirrorlog.server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.lavajuno.mirrorlog.io.OutputController;

/**
 * ServerThread serves a single client and queues events
 * for the OutputController to process.
 */
public class ServerThread extends Thread {

    /**
     * Input buffer size (in bytes)
     */
    private static final int LINE_BUFFER = 2048;

    /**
     * Boot clients that don't send anything for longer than this time (milliseconds)
     */
    private static final int SOCKET_TIMEOUT = 600000; // 10 minutes

    /**
     * Socket to communicate with client over
     */
    private final Socket socket;

    /**
     * The client's IP address
     */
    private final String client_address;

    /**
     * The ServerController's OutputController
     */
    private final OutputController outputController;

    /**
     * The connected client's component name
     */
    private String client_component_name;

    public ServerThread(Socket socket, OutputController outputController) {
        this.socket = socket;
        this.outputController = outputController;
        this.client_component_name = "(not specified)";
        this.client_address = socket.getInetAddress().toString();
    }

    @Override
    public void run() {
        try {
            outputController.submitEvent(
                    "Log Server",
                    0,
                    "Client at " + client_address + " connected."
            );
            socket.setSoTimeout(SOCKET_TIMEOUT);
            BufferedInputStream inFromClient = new BufferedInputStream(socket.getInputStream());
            BufferedOutputStream outToClient = new BufferedOutputStream(socket.getOutputStream());
            byte[] line_buf = new byte[LINE_BUFFER];
            int line_index = 0;
            // Read the first byte from the stream
            int next = inFromClient.read();
            // Loop while the stream is open
            while(next != -1) {
                // If we see a carriage return
                if(next == 13) {
                    // Read the next byte immediately
                    next = inFromClient.read();
                    // If the next byte is a line feed
                    if(next == 10) {
                        // Decode the portion of buffer that we have written
                        String line_str = new String(
                                Arrays.copyOf(line_buf, line_index),
                                StandardCharsets.UTF_8
                        );
                        // Set the severity to default (0)
                        int severity = 0;
                        // Catch the @ComponentName command
                        if(line_str.matches("^@ComponentName .{2,32}$")) {
                            client_component_name = line_str.substring("@ComponentName ".length());
                        } else { // This is a regular log event
                            // If a severity indicator is included in the event
                            if(line_str.matches("^[0-3].*$")) {
                                try {
                                    severity = Integer.parseInt(line_str.substring(0, 1));
                                } catch(NumberFormatException e) {
                                    System.err.println("how");
                                }
                                outputController.submitEvent(
                                        client_component_name,
                                        severity,
                                        line_str.substring(1)
                                );
                            } else { // If no severity indicator is included in the event
                                outputController.submitEvent(client_component_name, severity, line_str);
                            }
                            // Respond
                            outToClient.write((line_str + "\r\n").getBytes(StandardCharsets.UTF_8));
                            outToClient.flush();
                        }
                        // Reset the line index when we're done
                        line_index = 0;
                    }
                } else {
                    line_buf[line_index] = (byte) next;
                    line_index++;
                    if(line_index >= LINE_BUFFER) {
                        throw new IOException("Bad request.");
                    }

                }
                next = inFromClient.read();
            }
            outputController.submitEvent(
                    "Log Server",
                    0,
                    "Client at " + client_address + " disconnected."
            );
        } catch(SocketException e) {
            outputController.submitEvent(
                    "Log Server",
                    2,
                    "Client at " + client_address + " disconnected. (SocketException)"
            );
        } catch(IOException e) {
            outputController.submitEvent(
                    "Log Server",
                    2,
                    "Client at " + client_address + " disconnected. (IOException)"
            );
        } catch(Exception e) {
            outputController.submitEvent(
                    "Log Server",
                    2,
                    "Client at " + client_address + " disconnected. (Uncaught Exception)"
            );
            System.err.println(e.getMessage());
            System.err.println(Arrays.toString(e.getStackTrace()));
        }
    }
}
