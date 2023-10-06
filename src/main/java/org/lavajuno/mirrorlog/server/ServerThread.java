package org.lavajuno.mirrorlog.server;

import org.lavajuno.mirrorlog.client.ClientState;
import org.lavajuno.mirrorlog.io.OutputController;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ServerThread extends Thread {
    private final Socket socket;
    private final OutputController outputController;
    private final ClientState clientState;

    public ServerThread(Socket socket, OutputController outputController) {
        this.socket = socket;
        this.outputController = outputController;
        this.clientState = new ClientState();
    }

    @Override
    public void run() {
        try {
            socket.setSoTimeout(300000);
            BufferedInputStream inFromClient = new BufferedInputStream(socket.getInputStream());
            BufferedOutputStream outToClient = new BufferedOutputStream(socket.getOutputStream());
            byte[] line = new byte[8192];
            int line_index = 0;
            int next = inFromClient.read();
            while(next != -1) {
                if(next == 13) {
                    if(inFromClient.read() == 10) {
                        //outputController.submitEntry(
                        //        clientState.getComponentName(),
                        //        clientState.getSeverity(),
                        //        new String(line, StandardCharsets.UTF_8)
                        //);
                    }
                } else {
                    line[line_index] = (byte) next;
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
