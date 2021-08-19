package org.example.app;

import org.example.network.ClientConnection;
import org.example.pojo.Client;
import org.example.utils.StringVerifier;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Xiaotian
 * @program assignment1
 * @description
 * @create 2021-08-19 09:35
 */
public class ClientManager {
    private static ClientManager instance;
    private Map<String, Client> liveClients;
    private final ThreadPoolExecutor executor;

    public static synchronized ClientManager getInstance() {
        if (instance == null) {
            instance = new ClientManager();
        }

        return instance;
    }

    @SuppressWarnings("AlibabaThreadPoolCreation")
    private ClientManager() {
        liveClients = new HashMap<>();
        executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    /**
     * handle new incoming client connection
     * @param socket client socket
     */
    public void handleNewClientConnection(Socket socket) {
        String clientId = null;
        try {
            ClientConnection clientConnection = new ClientConnection(socket);

            // generate new client
            Client client = new Client();
            client.setClientConnection(clientConnection);

            // generate client id
            clientId = generateProperClientId();

            // if the string is not proper
            if (!StringVerifier.isValidClientId(clientId)) {
                System.out.println("ERROR: auto-generate new client Id.");
                System.out.println("clients full");
                clientConnection.closeMe();
                throw new IOException("client Not created");
            }

            client.setFormerId("");
            client.setId(clientId);

            // put this new client to MainHall
            client.setFormerRoomId("");
            client.setRoomId("MailHall");

            // store the client object reference in connection
            clientConnection.setClient(client);

            // put the connection into pool
            executor.execute(clientConnection);

        } catch (IOException e) {
            System.out.println("ERROR: handle new client connection");
            e.printStackTrace();
        }
    }

    /**
     * update client id
     * @return success
     */
    public synchronized boolean updateClientId(Client client, String newId) {

        // check validaty of the newId
        if (StringVerifier.isValidClientId(newId)) {
            String OriginalId = client.getId();

            if (liveClients.containsKey(OriginalId) && !liveClients.containsKey(newId)) {
                // change reference hashmap
                liveClients.put(newId,liveClients.remove(OriginalId));

                // change client itself
                client.setId(newId);
                client.setFormerId(OriginalId);

                return true;
            }
        }

        System.out.println("update client id failed");
        return false;
    }

    /**
     * remove a client object from living clients
     * @return success
     */
    public synchronized boolean removeClientFromLiveClients(Client client) {
        Client res = null;
        res = liveClients.remove(client.getId());

        if (res != null) {
            res = null;
            return true;
        } else {
            System.out.println("remove client failed");
            return false;
        }
    }

    /**
     * the server generates a unique id for the client which is guest
     * followed by the smallest integer greater than 0 that is
     * currently not in use by any other connected client, e.g. guest5
     * @return
     */
    private String generateProperClientId() {
        int smallestNumber = Integer.MAX_VALUE;
        // match guest[number]
        String regex = "^guest+[1-9]\\d*$";
        ArrayList<Integer> existingIdNumber = new ArrayList<>();

        // firstly, find the smallest id
        for (String id : liveClients.keySet()) {
            if (id.matches(regex)) {
                // remains number
                String strNoChars = id.substring(4);
                int number = Integer.parseInt(strNoChars);
                existingIdNumber.add(number);
                if (number < smallestNumber) {
                    smallestNumber = number;
                }
            }
        }

        // if smallestNumber is still max, just start with guest 1
        if (smallestNumber == Integer.MAX_VALUE) {
            return "guest1";
        }

        // start from smallest, then check the existence of the current id
        // e.g.  1,2,4,5,7,8; smallest is 1, 1 exists, 2 exists, 3 not, is 3.
        while(existingIdNumber.contains(smallestNumber)) {
            smallestNumber += 1;
        }

        return ("guest" + smallestNumber);
    }
}