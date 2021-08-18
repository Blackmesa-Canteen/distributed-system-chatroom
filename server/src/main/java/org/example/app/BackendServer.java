package org.example.app;

import org.example.network.NetworkHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Xiaotian
 * @program assignment1
 * @description server software
 * @create 2021-08-18 20:28
 */
public class BackendServer extends Thread {
    private int port;
    private boolean isRunning = false;

    private NetworkHandler handler;
    private ServerSocket serverSocket;

    public BackendServer(int port) {
        this.port = port;

    }

    @Override
    public void run() {
        runServer();
    }

    /**
     * run the server
     */
    public void runServer() {
        try {
            serverSocket = new ServerSocket(port);

            // infinitely run the server
            for(;;) {
                Socket newClientConnection = serverSocket.accept();
                if (newClientConnection != null) {
                    // serve the incoming client connection
                    handler.handle(newClientConnection);
                }
            }
        } catch (IOException e) {
            System.out.println("ERROR in accept client connection.");
            e.printStackTrace();
        } finally {
            closeServer();
        }
    }

    public void closeServer() {
        try {
            if ( serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("ERROR in closing the server.");
            e.printStackTrace();
        }

    }

    /**
     * check whether the server software is running
     * @return true:running
     */
    public boolean isRunning() {
        return isRunning;
    }
}