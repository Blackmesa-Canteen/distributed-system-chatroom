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

                    /*
                    * The server replies with a RoomList message only to the client that was creating the room.
                    * If the room was created, then it will appear in the list. The client out puts either
                    * e. g . “Room jokes created.” or “Room jokes is invalid or already in use.”
                     */
                    client.getClientConnection().sentTextMessageToMe(RoomMsgService.genRoomListMsg());

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Joinng the room
     *
     * If roomid is invalid or non existent then client’s current room will not change.
     *
     * Otherwise the client’s current room will change to the requested room.
     *
     * If the room did not change then the server will send a RoomChange message only to the client
     * that requested the room change.
     *
     * If the room did change, then server will send a RoomChange message to all clients currently
     * in the requesting client’s current room and the requesting client’s requested room.
     *
     * If client is changing to the MainHall then the server will also send a RoomContents message to the client
     * ( for the MainHall ) and a RoomList message after the RoomChange message.
     *
     * @param targetRoomId roomId
     * @param client client object
     * @return success?
     */
    public boolean joinClientToRoom(String targetRoomId, Client client) {

        Room targetRoom = null;
        Room oldRoom = null;
        String previousRoomId = "";

        synchronized (liveRooms) {
            if (liveRooms.containsKey(targetRoomId)) {
                targetRoom = liveRooms.get(targetRoomId);
            } else {
                // room is not exist
                // if not change, sent roomChange message to client himself
                // former == requested
                client.getClientConnection().sentTextMessageToMe(
                        RoomMsgService.genRoomChangeMsg(client.getId(), client.getRoomId(), client.getRoomId()));

                return false;
            }

            if (targetRoom != null) {
                // check client existence, if already in the house, not change
                if (targetRoom.getClients().contains(client)) {

                    // if not change, sent roomChange message to client himself
                    // former == requested
                    client.getClientConnection().sentTextMessageToMe(
                            RoomMsgService.genRoomChangeMsg(client.getId(), client.getRoomId(), client.getRoomId()));

                    return false;
                }

                // get client old RoomId before join the new room
                previousRoomId = client.getRoomId();

                // register client to the new room
                targetRoom.getClients().add(client);

                // send message when join a room, both previous and new room;
                // for starting mainHall, solve duplicate broadcast message
                if (!client.getFormerRoomId().equals("")) {
                    // if not first join the server
                    broadcastMessageInRoom(previousRoomId,
                            RoomMsgService.genRoomChangeMsg(client.getId(), previousRoomId, targetRoomId),
                            client);
                    broadcastMessageInRoom(targetRoomId,
                            RoomMsgService.genRoomChangeMsg(client.getId(), previousRoomId, targetRoomId),
                            null);

                    oldRoom = liveRooms.get(previousRoomId);

                    // kick the client from oldRoom
                    oldRoom.getClients().remove(client);
                    // check whether the former room becomes empty
                    handleEmptyRoom(previousRoomId);
                } else {
                    // if first join the server
                    broadcastMessageInRoom(targetRoomId,
                            RoomMsgService.genRoomChangeMsg(client.getId(), "", targetRoomId),
                            null);
                }

                // if the room is mainhall
                if (targetRoomId.equals("MainHall")) {
                    /*
                    * If client is changing to the MainHall then the server will
                    * also send a RoomContents message to the client (for the MainHall)
                    * and a RoomList message after the RoomChange message
                     */
                    client.getClientConnection().sentTextMessageToMe(RoomMsgService.genRoomListMsg());
                }

                // client will get room content info
                client.getClientConnection().sentTextMessageToMe(RoomMsgService.genRoomContentMsg(targetRoomId));

            }
        }

        if (targetRoom != null) {
            // update client's info
            client.setRoomId(targetRoom.getRoomId());
            client.setFormerRoomId(previousRoomId);
            return true;
        }

        // if not change, sent roomChange message to client himself
        // former == requested
        client.getClientConnection().sentTextMessageToMe(
                RoomMsgService.genRoomChangeMsg(client.getId(), client.getRoomId(), client.getRoomId()));
        return false;
    }

    /**
     * The server will first treat this as if all users of the room had sent a RoomChange message to the MainHall.
     * Then the server will delete the room. The server replies with a RoomList message only to the client that
     * was deleting the room. If the room was deleted, then it will not appear in the list.
     *
     * @param client
     */
    public void deleteRoomFromOwner(Client client, String roomId) {
        synchronized (liveRooms) {
            Room room = liveRooms.get(roomId);

            if (room != null) {
                if (client.equals(room.getOwner())) {

                    // kick all users inside to the MainHall
                    List<Client> clients = room.getClients();

                    for(Client c : clients) {
                        joinClientToRoom("MainHall", c);
                    }

                    // remove room from liveRooms
                    liveRooms.remove(roomId);

                    // replies with a RoomList message only to the owner
                    String roomListMsg = RoomMsgService.genRoomListMsg();
                    client.getClientConnection().sentTextMessageToMe(roomListMsg);
                }
            }
        }
    }

    /**
     * if the client goes offline, unregister the client
     *
     * The server will remove the user from their current room,
     * sending an appropriate RoomChange message to all clients in that room.
     * The roomid of the RoomChange message will
     * be an empty string , to indicate the user is disconnecting.
     * Rooms owned by the user who is disconnecting are set to have an empty owner.
     *
     * When the server sends the RoomChange event to the disconnecting client,
     * then it can close the connection.
     *
     * When the client that is disconnecting receives the RoomChange message,
     * then it can close the connection.
     *
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
            // send message to other people when offline
            String roomId = client.getRoomId();
            String changeMessage = RoomMsgService.genRoomChangeMsg(client.getId(), roomId, "");
            broadcastMessageInRoom(roomId, changeMessage, null);

            liveRooms.get(roomId).getClients().remove(client);

            // remove total empty room here.
            handleEmptyRoom(roomId);
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
     *
     * The server will first treat this as if all users of the room
     * had sent a RoomChange message to the MainHall. Then the server
     * will delete the room. The server replies with a RoomList message
     * only to the client that was deleting the room. If the room was
     * deleted, then it will not appear in the list.
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

    public void sendRoomListMsgToClient(Client client) {
        synchronized (liveRooms) {
            client.getClientConnection().sentTextMessageToMe(RoomMsgService.genRoomListMsg());
        }
    }

    public void sendRoomContentMsgToClient(Client client, String roomId) {
        synchronized (liveRooms) {
            client.getClientConnection().sentTextMessageToMe(RoomMsgService.genRoomContentMsg(roomId));
        }
    }
}