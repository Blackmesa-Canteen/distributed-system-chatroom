package org.example.service;

import com.google.gson.Gson;
import org.example.app.ChatRoomManager;
import org.example.msgBean.*;
import org.example.pojo.Client;
import org.example.pojo.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Xiaotian
 * @program assignment1
 * @description
 * @create 2021-08-19 16:23
 */
public class RoomMsgService {

    private static final ChatRoomManager chatRoomManager = ChatRoomManager.getInstance();

    public static String genJoinRoomMsg(String roomId) {
        JoinRoomMessage jsonObject = new JoinRoomMessage();
        jsonObject.setRoomid(roomId);

        return new Gson().toJson(jsonObject) + "\n";
    }

    public static String genRoomChangeMsg(String identity, String former, String roomId) {
        RoomChangeMessage jsonObject = new RoomChangeMessage();
        jsonObject.setIdentity(identity);
        jsonObject.setFormer(former);
        jsonObject.setRoomid(roomId);

        return new Gson().toJson(jsonObject) + "\n";
    }

    /**
     * generate room content msg with given roomID
     * @param roomId room id
     * @return json string
     */
    public static String genRoomContentMsg(String roomId) {
        RoomContentsMessage jsonObject = new RoomContentsMessage();
        jsonObject.setRoomid(roomId);

        Room room = chatRoomManager.getLiveRooms().get(roomId);
        if (room == null) {
            // TODO:等老师的回复，怎么处理。
            return "ok\n";
        }
        ArrayList<Client> clients = room.getClients();
        Client ownerObj = room.getOwner();
        String ownerId = "";
        if (ownerObj != null) {
            ownerId = ownerObj.getId();
        }
        jsonObject.setOwner(ownerId);

        List<String> identities = new ArrayList<>();
        for(Client client : clients) {
            identities.add(client.getId());
        }
        jsonObject.setIdentities(identities);

        return new Gson().toJson(jsonObject) + "\n";
    }

    public static String genWhoMsg(String roomId) {
        WhoMessage jsonObject = new WhoMessage();
        jsonObject.setRoomid(roomId);

        return new Gson().toJson(jsonObject) + "\n";
    }

    /**
     * The RoomList message lists all room ids and the count of identities
     * @return
     */
    public static String genRoomListMsg() {
        RoomListMessage jsonObject = new RoomListMessage();
        Map<String, Room> liveRooms = chatRoomManager.getLiveRooms();
        List<RoomDTO> roomDTOS = new ArrayList<>();

        for(String key : liveRooms.keySet()) {
            Room room = liveRooms.get(key);
            RoomDTO jsonRoom = new RoomDTO();
            jsonRoom.setRoomid(room.getRoomId());
            jsonRoom.setCount(room.getClients().size());

            roomDTOS.add(jsonRoom);
        }

        jsonObject.setRooms(roomDTOS);
        return new Gson().toJson(jsonObject) + "\n";
    }

    /**
     * generate relay message.
     * @param clientId client id
     * @param content content of message
     * @return
     */
    public static String genRelayMsg(String clientId, String content) {
        RelayMessage jsonObject = new RelayMessage();
        jsonObject.setIdentity(clientId);
        jsonObject.setContent(content);

        return new Gson().toJson(jsonObject) + "\n";
    }
}