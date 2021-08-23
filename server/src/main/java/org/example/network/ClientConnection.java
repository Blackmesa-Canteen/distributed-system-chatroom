package org.example.network;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.example.app.ChatRoomManager;
import org.example.app.ClientManager;
import org.example.pojo.Client;
import org.example.service.ClientMsgService;
import org.example.service.RoomMsgService;
import org.example.utils.Constants;
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
    private final Socket socket;

    /** my client object */
    private Client client;

    /**
     * output
     */
    private final PrintWriter outWriter;

    /**
     * Input
     */
    private final BufferedReader inReader;

    private final ChatRoomManager chatRoomManager;
    private final ClientManager clientManager;

    private boolean isAlive = false;

    public ClientConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.outWriter = new PrintWriter(socket.getOutputStream());
        this.inReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.chatRoomManager = ChatRoomManager.getInstance();
        this.clientManager = ClientManager.getInstance();
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
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
        isAlive = true;

        while (isAlive) {
            try {
                // if everything is fine, be stopped here
                String inputString = inReader.readLine();

                // check in alive or not
                if(inputString != null) {
                    // handle the input
//                    chatRoomManager.broadcastMessageInRoom(client.getRoomId(), inputString + "\n", client);

                    try {
                        JSONObject requestDataObject = JSONObject.parseObject(inputString);
                        if (requestDataObject == null) {
                            throw new JSONException("JSON object parse ERROR");
                        }
                        String orderType = requestDataObject.getString("type");
                        if(orderType != null) {

                            /* identity change */
                            if (orderType.equals(Constants.IDENTITY_CHANGE_JSON_TYPE)) {
                                String newId = requestDataObject.getString("identity");
                                if (newId == null) {
                                    throw new JSONException("Missing attributes");
                                }
                                clientManager.updateClientId(this.client, newId);
                            }
                            /* join room */
                            else if (orderType.equals(Constants.JOIN_JSON_TYPE)) {
                                String roomId = requestDataObject.getString("roomid");
                                if (roomId == null) {
                                    throw new JSONException("Missing attributes");
                                }
                                chatRoomManager.joinClientToRoom(roomId, this.client);
                            }
                            /* who: get current room content */
                            else if (orderType.equals(Constants.WHO_JSON_TYPE)) {
                                String roomId = requestDataObject.getString("roomid");
                                if (roomId == null) {
                                    throw new JSONException("Missing attributes");
                                }
                                chatRoomManager.sendRoomContentMsgToClient(this.client, roomId);
                            }
                            /* get roomlist message */
                            else if (orderType.equals(Constants.LIST_JSON_TYPE)) {
                                chatRoomManager.sendRoomListMsgToClient(this.client);
                            }
                            /* create room */
                            else if (orderType.equals(Constants.CREATE_ROOM_JSON_TYPE)) {
                                String roomId = requestDataObject.getString("roomid");
                                if (roomId == null) {
                                    throw new JSONException("Missing attributes");
                                }
                                chatRoomManager.createNewEmptyRoom(roomId, this.client);
                            }
                            /* delete room */
                            else if (orderType.equals(Constants.DELETE_JSON_TYPE)) {
                                String roomId = requestDataObject.getString("roomid");
                                if (roomId == null) {
                                    throw new JSONException("Missing attributes");
                                }
                                chatRoomManager.deleteRoomFromOwner(this.client, roomId);
                            }
                            /* quit */
                            else if (orderType.equals(Constants.QUIT_JSON_TYPE)) {
                                isAlive = false;
                            }
                            /* messaging */
                            else if (orderType.equals(Constants.MESSAGE_JSON_TYPE)) {
                                String content = requestDataObject.getString("content");
                                if (content == null) {
                                    throw new JSONException("Missing attributes");
                                }

                                String broadMessage = RoomMsgService.genRelayMsg(this.client.getId(), content);
                                chatRoomManager.broadcastMessageInRoom(this.client.getRoomId(),
                                        broadMessage,
                                        this.client);
                            }

                            /* else */
                            else {
                                System.out.println("JSON's type is not defined.");
                            }

                        } else {
                            /* if not contains `type` key */
                            // debug
                            System.out.println("JSON's type is not defined.");
                        }
                    } catch (JSONException e) {
                        System.out.println("JSON Format error.");
                    }

                } else {
                    // the input stream shutted down
                    // lose connection with client
                    isAlive = false;
                }
            } catch (IOException e) {
                isAlive = false;
                System.out.println("ERROR: client connection run error");
                e.printStackTrace();
            }
        }

        this.closeMe();
    }

    /**
     * close this connection
     */
    public void closeMe() {
        try {
            ClientManager clientManager = ClientManager.getInstance();
            ChatRoomManager roomManager = ChatRoomManager.getInstance();

            // exit all the room
            roomManager.unregisterClientFromAllChatRoom(client);

            // remove it from alive lists
            clientManager.removeClientFromLiveClients(client);

            // close connection
            if (!socket.isClosed()) {
                socket.close();
                inReader.close();
                outWriter.close();
            }

            // destroy client object
            clientManager.destroyClient(client);
        } catch (IOException e) {
            System.out.println("ERROR: close client connection error.");
            e.printStackTrace();
        }
    }
}