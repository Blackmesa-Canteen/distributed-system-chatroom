package org.example.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Xiaotian
 * @program assignment1
 * @description The sending channel for server's full-duplex communication
 * @create 2021-08-18 22:57
 */
public class OutConnection extends Thread{
    private Socket socket;
    private PrintWriter writer;
    private boolean connectionAlive = false;

    public OutConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.writer = new PrintWriter(socket.getOutputStream());
    }

    @Override
    public void run() {
        connectionAlive = true;
        while (!isInterrupted() && connectionAlive) {

        }
    }
}