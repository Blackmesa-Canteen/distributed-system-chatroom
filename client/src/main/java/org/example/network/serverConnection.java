package org.example.network;

import org.example.utils.Encoders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Xiaotian
 * @program assignment1
 * @description
 * @create 2021-08-26 00:41
 */
public class serverConnection extends Thread{
    private final Socket socket;
    private String hostname;

    /**
     * Output
     */
    private final PrintWriter writer;

    /**
     * Input
     */
    private final BufferedReader reader;

    private boolean isAlive = false;

    public serverConnection(Socket socket, String hostname) throws IOException {
        this.socket = socket;
        this.hostname = hostname;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(socket.getOutputStream());
    }

    @Override
    public void run() {
        isAlive = true;

        while (isAlive) {
            /* 连接后,持续监听来自服务器的讯息，并处理之 */
            /* 客户端输入并发送给服务端的动作，应该需要定义另一个线程 */
        }
    }

    /**
     * sent UTF8 text to this connection instance.
     *
     * @param text UTF8 json text
     */
    public void sentTextMessageToMe(String text) {

        // skip null input
        if (text == null) {
            return;
        }

        try {
            String utf8 = Encoders.StringToUtf8(text);
            writer.print(utf8);
            writer.flush();
        } catch (Exception e) {
            System.out.println("sent Text Message To server ERROR:");
            e.printStackTrace();
        }
    }

    /**
     * close this connection
     */
    public void closeMe() {
        // close connection
        if (!socket.isClosed()) {
            try {
                socket.close();
                reader.close();
                writer.close();
            } catch (IOException e) {
                System.out.println("ERROR: close server connection error.");
                e.printStackTrace();
            }

        }
    }
}