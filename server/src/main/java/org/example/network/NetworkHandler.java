package org.example.network;

import java.net.Socket;

/**
 * @author Xiaotian
 * @program assignment1
 * @description
 * @create 2021-08-18 20:30
 */
public interface NetworkHandler {

    /**
     * handle a connection
     * @param socket
     */
    void handle(Socket socket);
}