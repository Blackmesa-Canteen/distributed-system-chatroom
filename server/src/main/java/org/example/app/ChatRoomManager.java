package org.example.app;

import org.example.pojo.Client;
import org.example.pojo.Room;
import org.example.service.RoomMsgService;
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
    private final Map<String, Room> liveRooms;

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
    public Room findRoomById(String roomId) {
        synchronized (liveRooms) {
            return liveRooms.get(roomId);
        }
    }

    /**
     * get room pojo list
     * @return room pojo list
     */
    public List<Room> getRoomList() {
        synchronized (liveRooms) {
            List<Room> roomList = new ArrayList<>();
            for (String key : liveRooms.keySet()) {
                roomList.add(liveRooms.get(key));
            }

            return roomList;
        }
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
    public boolean createNewEmptyRoom(String roomId, Client client) {

        // check names
        if (StringVerifier.isValidRoomId(roomId) &&
                StringVerifier.isValidClientId(client.getId())) {

            synchronized (liveRooms) {
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
        }

        return false;
    }

    /**
     * Joing the room
     * @param roomId roomId
     * @param client client object
     * @return success?
     */
    public boolean joinClientToRoom(String roomId, Client client) {

        Room room = null;
        String previousRoomId = "";

        synchronized (liveRooms) {
            if (liveRooms.containsKey(roomId)) {
                room = liveRooms.get(roomId);
            } else {
                // room is not exist
                // if not change, sent roomChange message to client himself
                client.getClientConnection().sentTextMessageToMe(
                        RoomMsgService.genRoomChangeMsg(client.getId(), client.getRoomId(), roomId));

                return false;
            }

            if (room != null) {
                // check client existence, if already in the house, not change
                if (room.getClients().contains(client)) {

                    // if not change, sent roomChange message to client himself
                    client.getClientConnection().sentTextMessageToMe(
                            RoomMsgService.genRoomChangeMsg(client.getId(), client.getRoomId(), roomId));

                    return false;
                }

                // get client current RoomId before join the new room
                previousRoomId = client.getRoomId();

                // register client to the new room
                room.getClients().add(client);

                // send message when join a room, both previous and new room;
                // for starting mainHall, solve duplicate broadcast message
                if (!client.getFormerRoomId().equals("")) {
                    broadcastMessageInRoom(previousRoomId,
                            RoomMsgService.genRoomChangeMsg(client.getId(), previousRoomId, roomId),
                            null);
                    broadcastMessageInRoom(roomId,
                            RoomMsgService.genRoomChangeMsg(client.getId(), previousRoomId, roomId),
                            null);
                } else {
                    broadcastMessageInRoom(roomId,
                            RoomMsgService.genRoomChangeMsg(client.getId(), "", roomId),
                            null);
                }

                // if the room is mainhall
                if (roomId.equals("MainHall")) {
                    /*
                    * If client is changing to the MainHall then the server will
                    * also send a RoomContents message to the client (for the MainHall)
                    * and a RoomList message after the RoomChange message
                     */
                    client.getClientConnection().sentTextMessageToMe(RoomMsgService.genRoomListMsg());
                }

                // client will get room content info
                client.getClientConnection().sentTextMessageToMe(RoomMsgService.genRoomContentMsg(roomId));
            }
        }

        if (room != null) {
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
    public boolean removeClientFromRoom(String roomId, Client client) {

        Room room = null;

        synchronized (liveRooms) {
            // check existence
            if (liveRooms.containsKey(roomId)) {
                room = liveRooms.get(roomId);
            }

            if (room != null) {
                // check client existence
                if (!room.getClients().contains(client)) {
                    return false;
                }

                // TODO: send message when quit a Room
                room.getClients().remove(client);

                // remove total empty room.
                handleEmptyRoom(roomId);
            }
        }

        if (room != null) {
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
    public void unregisterClientFromAllChatRoom(Client client) {

        synchronized (liveRooms) {
            // check the ownership and kick the client
            for (String roomId : liveRooms.keySet()) {
                Room room = liveRooms.get(roomId);
                if (room.getOwner() != null && room.getOwner().equals(client)) {
                    room.setOwner(null);
                }
            }

            // kick the client
            // TODO: send message to other people when offline
            String roomId = client.getRoomId();
            liveRooms.get(roomId).getClients().remove(client);

            // remove total empty room here.
            handleEmptyRoom(roomId);
        }
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
    public void destroyRoomFromLiveRooms(String roomId) {

        synchronized (liveRooms) {
            Room room = liveRooms.get(roomId);
            if (room != null) {
                room.setRoomId(null);
                room.setOwner(null);
                room.setClients(null);
                liveRooms.remove(roomId);
            }
        }
    }

    /**
     * Boradcast a message in a specific room.
     * @param roomId roomId
     * @param message message
     * @param clientExcluded excluded client
     */
    public void broadcastMessageInRoom(String roomId, String message, Client clientExcluded) {
        synchronized (liveRooms) {
            Room room = liveRooms.get(roomId);
            if (room != null ) {
                ArrayList<Client> clients = room.getClients();

                for(Client client : clients) {
                    if(clientExcluded == null || !clientExcluded.equals(client)) {
                        client.getClientConnection().sentTextMessageToMe(message);
                    }
                }
            }
        }
    }

    /**
     * If any room other than MainHall has an empty owner and becomes
     * empty ( i.e. has no contents ) then the room is deleted.
     */
    public void handleEmptyRoom(String roomId) {
        Room thisRoom = liveRooms.get(roomId);

        if (!roomId.equals("MainHall") &&
                thisRoom.getOwner() == null &&
                thisRoom.getClients().size() == 0) {

            // delete the room
            liveRooms.remove(roomId);
            thisRoom.setOwner(null);
            thisRoom.setClients(null);
            thisRoom.setRoomId(null);
        }
    }

    public Map<String, Room> getLiveRooms() {
        return liveRooms;
    }
}