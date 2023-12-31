package org.lavajuno.mirrorlog.server;

import java.io.*;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.lavajuno.mirrorlog.config.ApplicationConfig;
import org.lavajuno.mirrorlog.io.OutputController;
import org.lavajuno.mirrorlog.main.LogMap;

/**
 * ServerThread serves a single client and queues events
 * for the OutputController to process.
 */
public class ServerThread extends Thread {
    private final Socket socket;
    private final InetAddress client_address;
    private final OutputController outputController;
    private final ApplicationConfig application_config;

    /**
     * Instantiates a ServerThread.
     * @param socket Socket to communicate with client over
     * @param outputController OutputController to queue events in
     * @param application_config ApplicationConfig to use
     */
    public ServerThread(Socket socket, OutputController outputController, ApplicationConfig application_config) {
        this.socket = socket;
        this.outputController = outputController;
        this.client_address = socket.getInetAddress();
        this.application_config = application_config;
        Runtime.getRuntime().addShutdownHook(new Thread(this::interrupt));
    }

    @Override
    public void run() {
        try {
            if(application_config.isRestricted() &&
                    !application_config.getAllowedAddresses().contains(client_address)) {
                socket.close();
                return;
            }
            outputController.submitEvent(
                    "Log Server",
                    0,
                    "Client at " + client_address + " connected."
            );
            socket.setSoTimeout(application_config.getTimeout());
            BufferedInputStream inFromClient = new BufferedInputStream(socket.getInputStream());
            BufferedOutputStream outToClient = new BufferedOutputStream(socket.getOutputStream());
            byte[] line_buf = new byte[LogMap.INPUT_BUFFER_SIZE];
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
                        if(line_str.matches("^@[0-9A-Za-z_-]{1,128}@[0-3].*$")) {
                            String[] fragments = line_str.split("@", 3);
                            // Submit log event and respond
                            outputController.submitEvent(
                                    fragments[1],
                                    Integer.parseInt(fragments[2].substring(0, 1)),
                                    fragments[2].substring(1)
                            );
                            outToClient.write((line_str + "\r\n").getBytes(StandardCharsets.UTF_8));
                            outToClient.flush();
                        } else {
                            outToClient.write("BAD SYNTAX\r\n".getBytes(StandardCharsets.UTF_8));
                            outToClient.flush();
                        }
                        // Reset the line index when we're done
                        line_index = 0;
                    }
                } else {
                    // Append current byte to buffer and increment index
                    line_buf[line_index] = (byte) next;
                    line_index++;
                    if(line_index >= LogMap.INPUT_BUFFER_SIZE) {
                        throw new IOException("Bad request (Too long).");
                    }
                }
                // Read next byte
                next = inFromClient.read();
            }
            socket.close();
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
        }
    }

    @Override
    public void interrupt() {
        try {
            System.out.println("Connection to " + socket.getInetAddress() + " terminated. (Shutdown)");
            this.socket.close();
        } catch(IOException e) {
            System.err.println("Failed to close connection to " + socket.getInetAddress());
        }
    }
}
