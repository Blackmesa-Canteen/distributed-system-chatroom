package org.example.app;

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
    private Map<String, Room> rooms;

    public static synchronized ChatRoomManager getInstance() {
        if (instance == null) {
            instance = new ChatRoomManager();
        }

        return instance;
    }

    private ChatRoomManager() {
        // create main chat room
        Room room = new Room();
        room.setRoomId("MainHall");
        room.setOwnerId("");
        room.setGuestConnections(new HashMap<>());

        rooms.put("MainHall", room);
    }

    /**
     * get room pojo with roomId
     * @param roomId room id
     * @return room pojo
     */
    public synchronized Room findRoomById(String roomId) {
        return rooms.get(roomId);
    }

    /**
     * get room pojo list
     * @return room pojo list
     */
    public synchronized List<Room> getRoomList() {
        List<Room> roomList = new ArrayList<>();
        for (String key : rooms.keySet()) {
            roomList.add(rooms.get(key));
        }

        return roomList;
    }

    /**
     * create a empty room without any clients.
     * @param roomId must contain alphanumeric characters only ,
     *               start with an upper or lower case letter,
     *               have at least 3 characters and at most 32 characters.
     *
     * @param ownerId an alphanumeric string starting with an upper or lower
     *                case character. must be at least 3 characters and no more
     *                than 16 characters.
     *
     * @return true: success, need “Room jokes created"; false, need “Room jokes is invalid or already in use.”
     */
    public synchronized boolean createNewEmptyRoom(String roomId, String ownerId) {

        // check names
        if (StringVerifier.isValidRoomId(roomId) &&
                StringVerifier.isValidClientId(ownerId)) {

            // check existence
            if (!rooms.containsKey(roomId)) {
                Room room = new Room();
                room.setRoomId(roomId);
                room.setOwnerId(ownerId);
                room.setGuestConnections(new HashMap<>());

                rooms.put(roomId, room);
            }
        }

        return false;
    }

    /**
     * The server will ﬁrst treat this as if all users of the room
     * had sent a RoomChange message to the MainHall. Then the server
     * will delete the room. The server replies with a RoomList message
     * only to the client that was deleting the room. If the room was
     * deleted, then it will not appear in the list.
     *
     * @param roomId the room id that will be deleted
     * @return successful
     */
    public synchronized boolean deleteRoomById(String roomId) {

        if (rooms.containsKey(roomId)) {
            ;
        }


        return false;
    }
}