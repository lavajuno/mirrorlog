package org.lavajuno.mirrorlog.server;

import org.lavajuno.mirrorlog.io.OutputController;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ServerThread extends Thread {
    private final Socket socket;
    private final OutputController outputController;
    private String client_component_name;

    public ServerThread(Socket socket, OutputController outputController) {
        this.socket = socket;
        this.outputController = outputController;
        this.client_component_name = "(not specified)";
    }

    @Override
    public void run() {
        try {
            socket.setSoTimeout(300000);
            BufferedInputStream inFromClient = new BufferedInputStream(socket.getInputStream());
            BufferedOutputStream outToClient = new BufferedOutputStream(socket.getOutputStream());
            byte[] line_buf = new byte[8192];
            int line_index = 0;

            // (blocking) read first byte of stream
            int next = inFromClient.read();
            // loop while stream is open
            while(next != -1) {
                // handle presence of CR
                if(next == 13) {
                    if(inFromClient.read() == 10) {
                        // decode portion of buffer we have written
                        String line_str = new String(
                                Arrays.copyOf(line_buf, line_index),
                                StandardCharsets.UTF_8
                        );
                        int severity = 0;
                        // catch @ComponentName command
                        if(line_str.matches("^@ComponentName [A-Za-z0-9_-]{2,32}$")) {
                            client_component_name = line_str.substring("@ComponentName ".length());
                        } else {
                            // Set severity if it is included
                            if(line_str.matches("^[0-3].*$")) { // assign severity if it is included
                                try {
                                    severity = Integer.parseInt(line_str.substring(0, 1));
                                    line_str = line_str.substring(1);
                                } catch(NumberFormatException e) {
                                    System.err.println("how");
                                }
                            }
                            // Log and return output
                            try {
                                outputController.submitEntry(client_component_name, severity, line_str);
                                outToClient.write(line_str.getBytes(StandardCharsets.UTF_8));
                                outToClient.flush();
                            } catch(IOException e) {
                                outToClient.write("FAIL".getBytes(StandardCharsets.UTF_8));
                                outToClient.flush();
                            }
                        }


                        // reset line index
                        line_index = 0;
                    }
                } else {
                    line_buf[line_index] = (byte) next;
                    line_index++;
                }
                next = inFromClient.read();
            }

        } catch(SocketException e) {
            //TODO implement
        } catch(IOException e) {
            //TODO implement
        } catch(Exception e) {
            System.err.println("ServerThread encountered an unhandled exception!");
            System.err.println(e.getMessage());
            System.err.println(Arrays.toString(e.getStackTrace()));
        }
    }
}
