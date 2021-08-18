package org.example.network;

import org.example.utils.Encoders;

import java.io.*;
import java.net.Socket;

/**
 * @author Xiaotian
 * @program assignment1
 * @description The tunnel for server full-duplex communication
 * @create 2021-08-18 23:39
 */
public class ClientConnection implements Runnable {

    /** server socket */
    private Socket socket;

    /**
     * output
     */
    private PrintWriter outWriter;

    /**
     * Input
     */
    private BufferedReader inReader;

    public ClientConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.outWriter = new PrintWriter(socket.getOutputStream());
        this.inReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * sent UTF8 text to this connection instance.
     *
     * @param text UTF8 json text
     */
    public void sentTextMessageToMe(String text) {
        try {
            String utf8 = Encoders.StringToUtf8(text);
            outWriter.print(utf8);
            outWriter.flush();
        } catch (Exception e) {
            System.out.println("sent Text Message To client ERROR:");
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

    }

    /**
     * close this connection
     */
    public void closeMe() {

    }


}