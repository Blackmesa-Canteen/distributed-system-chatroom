package org.example.app;

import org.example.pojo.Client;
import org.example.pojo.Room;
import org.example.utils.StringVerifier;

import java.util.*;

/**
 * @author Xiaotian
 * @program assignment1
 * @description The singleton container that holds all rooms
 * @create 2021-08-19 00:07
 */
public class ChatRoomManager {

    /** constants */

    private static ChatRoomManager instance;
    private Map<String, Room> liveRooms;

    public static synchronized ChatRoomManager getInstance() {
        if (instance == null) {
            instance = new ChatRoomManager();
        }

        return instance;
    }

    private ChatRoomManager() {

        liveRooms = new HashMap<>();

        // create main chat room
        Room room = new Room();
        room.setRoomId("MainHall");
        room.setOwner(null);
        room.setClients(new ArrayList<>());

        liveRooms.put("MainHall", room);
    }

    /**
     * get room pojo with roomId
     * @param roomId room id
     * @return room pojo
     */
    public synchronized Room findRoomById(String roomId) {
        return liveRooms.get(roomId);
    }

    /**
     * get room pojo list
     * @return room pojo list
     */
    public synchronized List<Room> getRoomList() {
        List<Room> roomList = new ArrayList<>();
        for (String key : liveRooms.keySet()) {
            roomList.add(liveRooms.get(key));
        }

        return roomList;
    }

    /**
     * create a empty room without any clients.
     * @param roomId must contain alphanumeric characters only ,
     *               start with an upper or lower case letter,
     *               have at least 3 characters and at most 32 characters.
     *
     * @param client client object, whose id is an alphanumeric string starting with an upper or lower
     *                case character. must be at least 3 characters and no more
     *                than 16 characters.
     *
     * @return true: success, need “Room jokes created"; false, need “Room jokes is invalid or already in use.”
     */
    public synchronized boolean createNewEmptyRoom(String roomId, Client client) {

        // check names
        if (StringVerifier.isValidRoomId(roomId) &&
                StringVerifier.isValidClientId(client.getId())) {

            // check existence
            if (!liveRooms.containsKey(roomId)) {
                Room room = new Room();
                room.setRoomId(roomId);
                room.setOwner(client);
                room.setClients(new ArrayList<>());

                // register owner to the room
                // room.getClients().put(client.getId(), client);

                // register room to liveRooms
                liveRooms.put(roomId, room);
                return true;
            }
        }

        return false;
    }

    /**
     * The server will first treat this as if all users of the room
     * had sent a RoomChange message to the MainHall. Then the server
     * will delete the room. The server replies with a RoomList message
     * only to the client that was deleting the room. If the room was
     * deleted, then it will not appear in the list.
     *
     * @param roomId the room id that will be deleted
     * @return successful
     */
    public synchronized boolean deleteRoomById(String roomId) {

        if (liveRooms.containsKey(roomId)) {
            ;
        }


        return false;
    }

    /**
     * Joing the room
     * @param roomId roomId
     * @param client client object
     * @return success?
     */
    public synchronized boolean joinClientToRoom(String roomId, Client client) {
        // check existence
        if (liveRooms.containsKey(roomId)) {
            Room room = liveRooms.get(roomId);

//            for(Client c : room.getClients()) {
//                if (c.getId().equals(client.getId())) {
//                    return false;
//                }
//            }

            // check client existence
            if (room.getClients().contains(client)) {
                return false;
            }

            // get client current RoomId before join the new room
            String previousRoomId = client.getRoomId();

            // register client to the new room
            room.getClients().add(client);

            // update client's info
            client.setRoomId(room.getRoomId());
            client.setFormerRoomId(previousRoomId);
            return true;
        }

        return false;
    }

    /**
     * remove the client to the MainHall
     * @param roomId room id that the client is kicked
     * @param client client object
     * @return success
     */
    public synchronized boolean removeClientFromRoom(String roomId, Client client) {
        // check existence
        if (liveRooms.containsKey(roomId)) {
            Room room = liveRooms.get(roomId);

            // check client existence
            if (!room.getClients().contains(client)) {
                return false;
            }

            // register client to the new room
            room.getClients().remove(client);

            // update client's info
            client.setRoomId("MainHall");
            client.setFormerRoomId(roomId);
            return true;
        }

        return false;
    }

    /**
     * if the client goes offline, unresgiter the client
     * @param client
     */
    public synchronized void unregisterClientFromAllChatRoom(Client client) {
        // check the ownership and kick the client
        for (String roomId : liveRooms.keySet()) {
            Room room = liveRooms.get(roomId);
            if (room.getOwner().equals(client)) {
                room.setOwner(null);
            }
        }

        // kick the client
        String roomId = client.getRoomId();
        liveRooms.get(roomId).getClients().remove(client);
    }


    /**
     * destroy a room from living rooms
     * @param roomId
     */
    public synchronized void destroyRoomFromLiveRooms(String roomId) {
        Room room = liveRooms.get(roomId);
        if (room != null) {
            room.setRoomId(null);
            room.setOwner(null);
            room.setClients(null);
            liveRooms.remove(roomId);
        }
    }

}