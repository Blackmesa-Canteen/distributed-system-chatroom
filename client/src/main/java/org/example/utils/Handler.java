package org.example.utils;

import com.google.gson.Gson;

import java.util.*;

import org.example.msgBean.*;
import org.example.pojo.Client;

import javax.xml.stream.events.Comment;

public class Handler {
    private static final JsonEncoder JE = new JsonEncoder();

    public void Initialize(String JsonMessage, Client client){
        Map<String,String> map  =  JE.Decode(JsonMessage);//decode message to map
        String type = map.get("type");
        switch(type){
            case "newidentity":
                //make change to local client object
                client.setId(map.get("identity"));
                client.setFormerId(map.get("former"));
                break;
            case "roomchange":
                String roomchange_identity = map.get("identity");
                String former = map.get("former");
                String roomid = map.get("roomid");

                //make change to local client object
                client.setRoomId(roomid);
                client.setFormerRoomId(former);
                break;
            case "roomcontents":
                //make change to local client object
                client.setTempRoomContent(handleRoomContentsMessage(JsonMessage));
                break;
            case "roomlist":
                Gson gson = new Gson();
                RoomListMessage RLM = gson.fromJson(JsonMessage,RoomListMessage.class);
                client.setRoomDTOList(RLM.getRooms());
                break;
        }

        //check if initialization has completed
        if(client.getId()!=null && client.getRoomId()!=null && client.getRoomDTOList()!=null && client.getTempRoomContent() !=null){

            System.out.println("Connected to " + client.getServerConnection().gethostname() + " as " + client.getId());
            System.out.println(client.printroomDTOlist());
            System.out.println(client.getId() + " moves to " + client.getRoomId());
            System.out.println(client.getTempRoomContent());
            //Initialization finished, set client status to commonstatus
            client.setStatus(Constants.COMMON_STATUS);
        }
    }

    public void handle(String JsonMessage, Client client){
        Map<String,String> map  =  JE.Decode(JsonMessage);//decode message to map
        String result = "";
        if(map.get("type").equals(Constants.MESSAGE_JSON_TYPE)){//identify message or command
            result += handleMessage(map);
        }else{
            result += handleCommand(map,client,JsonMessage);
        }

        if(!result.equals("")){
            System.out.println(result);
            //the next status is still Commonstatus
            client.setStatus(Constants.COMMON_STATUS);
        }else{
            //error happen set client status to Close
            System.out.println("error happen in Common Status");
            client.setStatus(Constants.CLOSE);
        }
    }

    public String handleMessage(Map<String,String> map){
        return (map.get("identity")+": "+map.get("content"));
    }

    public String handleCommand(Map<String,String> map, Client client,String JsonMessage){
        String type = map.get("type");
        String result = "";
        switch(type){
            case "newidentity":
                result = handleNewIdentityMessage(map,client);
                break;
            case "roomchange":
                result = handleRoomChangeMessage(map,client);
                break;
            case "roomcontents":
                result = handleRoomContentsMessage(JsonMessage);
                client.setTempRoomContent(result);
                //client.setStatus(Constants.COMMON_STATUS);
                break;
            case "roomlist":
                Gson gson = new Gson();
                RoomListMessage RLM = gson.fromJson(JsonMessage,RoomListMessage.class);
                client.setRoomDTOList(RLM.getRooms());
                result = client.printroomDTOlist();
                break;
            default:
                System.out.println("Command Error");
        }
        return result;
    }

    public void waitCreateResponse(String JsonMessage, Client client){
        Map<String,String> map  =  JE.Decode(JsonMessage);//decode message to map
        String result = "";
        if(map.get("type").equals(Constants.MESSAGE_JSON_TYPE)){//identify message or command
            result += handleMessage(map);
        }else{
            String type = map.get("type");
            switch(type){
                case "newidentity":
                    result = handleNewIdentityMessage(map,client);
                    break;
                case "roomchange":
                    result =  handleRoomChangeMessage(map,client);
                    break;
                case "roomcontents":
                    result = handleRoomContentsMessage(JsonMessage);
                    client.setTempRoomContent(result);
                    break;
                case "roomlist":
                    Gson gson = new Gson();
                    RoomListMessage RLM = gson.fromJson(JsonMessage,RoomListMessage.class);
                    boolean success = CreateRoomSuccess(client,RLM);
                    if(success){
                        result = "Room " + client.getTempRoomName() + " created";
                    }else{
                        result = "Room " + client.getTempRoomName() + " is invalid or already in use";
                    }
                    //update local client variables
                    client.setRoomDTOList(RLM.getRooms());
                    client.setTempRoomName(null);
                    //roomcreate request has been processed, back to commonstatus
                    client.setStatus(Constants.COMMON_STATUS);
                    break;
                default:
                    System.out.println("Command Error");
            }
        }
        System.out.println(result);
    }

    public void waitDeleteResponse(String JsonMessage, Client client){
        Map<String,String> map  =  JE.Decode(JsonMessage);//decode message to map
        String result = "";
        if(map.get("type").equals(Constants.MESSAGE_JSON_TYPE)){//identify message or command
            result += handleMessage(map);
        }else{
            String type = map.get("type");
            switch(type){
                case "newidentity":
                    result = handleNewIdentityMessage(map,client);
                    break;
                case "roomchange":
                    result = handleRoomChangeMessage(map,client);
                    break;
                case "roomcontents":
                    result = handleRoomContentsMessage(JsonMessage);
                    client.setTempRoomContent(result);
                    break;
                case "roomlist":
                    Gson gson = new Gson();
                    RoomListMessage RLM = gson.fromJson(JsonMessage,RoomListMessage.class);
                    boolean success = DeleteRoomSuccess(client,RLM);
                    if(success){
                        result = "Room " + client.getTempRoomName() + " delete success";
                    }else{
                        result = "Room " + client.getTempRoomName() + " delete fails";
                    }
                    //update local client variables
                    client.setRoomDTOList(RLM.getRooms());
                    client.setTempRoomName(null);
                    //roomcreate request has been processed, back to commonstatus
                    client.setStatus(Constants.COMMON_STATUS);
                    break;
                default:
                    System.out.println("Command Error");
            }
        }
        System.out.println(result);
    }

    public void waitListResponse(String JsonMessage, Client client){
        Map<String,String> map  =  JE.Decode(JsonMessage);//decode message to map
        String result = "";
        if(map.get("type").equals(Constants.MESSAGE_JSON_TYPE)){//identify message or command
            result += handleMessage(map);
        }else{
            String type = map.get("type");
            switch(type){
                case "newidentity":
                    result = handleNewIdentityMessage(map,client);
                    break;
                case "roomchange":
                    result = handleRoomChangeMessage(map,client);
                    break;
                case "roomcontents":
                    result = handleRoomContentsMessage(JsonMessage);
                    client.setTempRoomContent(result);
                    client.setStatus(Constants.COMMON_STATUS);
                    break;
                case "roomlist":
                    Gson gson = new Gson();
                    RoomListMessage RLM = gson.fromJson(JsonMessage,RoomListMessage.class);
                    //update local client variables
                    client.setRoomDTOList(RLM.getRooms());
                    //list request has been processed, back to commonstatus
                    result = client.printroomDTOlist();
                    client.setStatus(Constants.WAIT_LIST_STATUS);
                    break;
                default:
                    System.out.println("Command Error");
            }
        }
        System.out.println(result);
    }

    public void waitQuitResponse(String JsonMessage, Client client){
        Map<String,String> map  =  JE.Decode(JsonMessage);//decode message to map
        String result = "";
        if(map.get("type").equals(Constants.MESSAGE_JSON_TYPE)){//identify message or command
            result += handleMessage(map);
        }else{
            String type = map.get("type");
            switch(type){
                case "newidentity":
                    result = handleNewIdentityMessage(map,client);
                    break;
                case "roomchange":
                    result = handleRoomChangeMessage(map,client);
                    client.setStatus(Constants.CLOSE);
                    break;
                case "roomcontents":
                    result = handleRoomContentsMessage(JsonMessage);
                    client.setTempRoomContent(result);
                    break;
                case "roomlist":
                    Gson gson = new Gson();
                    RoomListMessage RLM = gson.fromJson(JsonMessage,RoomListMessage.class);
                    client.setRoomDTOList(RLM.getRooms());
                    result = client.printroomDTOlist();
                    break;
                default:
                    System.out.println("Command Error");
            }
        }
        System.out.println(result);
    }

    public void waitJoinResponse(String JsonMessage, Client client){
        Map<String,String> map  =  JE.Decode(JsonMessage);//decode message to map
        String result = "";
        if(map.get("type").equals(Constants.MESSAGE_JSON_TYPE)){//identify message or command
            result += handleMessage(map);
        }else{
            String type = map.get("type");
            switch(type){
                case "newidentity":
                    result = handleNewIdentityMessage(map,client);
                    break;
                case "roomchange":
                    result = handleRoomChangeMessage(map,client);
                    break;
                case "roomcontents":
                    result = handleRoomContentsMessage(JsonMessage);
                    client.setTempRoomContent(result);
                    break;
                case "roomlist":
                    Gson gson = new Gson();
                    RoomListMessage RLM = gson.fromJson(JsonMessage,RoomListMessage.class);
                    client.setRoomDTOList(RLM.getRooms());
                    result = client.printroomDTOlist();
                    break;
                default:
                    System.out.println("Command Error");
            }
        }
        System.out.println(result);
    }

    public void waitIDChangeResponse(String JsonMessage, Client client){
        Map<String,String> map  =  JE.Decode(JsonMessage);//decode message to map
        String result = "";
        if(map.get("type").equals(Constants.MESSAGE_JSON_TYPE)){//identify message or command
            result += handleMessage(map);
        }else{
            String type = map.get("type");
            switch(type){
                case "newidentity":
                    result = handleNewIdentityMessage(map,client);
                    break;
                case "roomchange":
                    result = handleRoomChangeMessage(map,client);
                    break;
                case "roomcontents":
                    result = handleRoomContentsMessage(JsonMessage);
                    client.setTempRoomContent(result);
                    break;
                case "roomlist":
                    Gson gson = new Gson();
                    RoomListMessage RLM = gson.fromJson(JsonMessage,RoomListMessage.class);
                    client.setRoomDTOList(RLM.getRooms());
                    result = client.printroomDTOlist();
                    break;
                default:
                    System.out.println("Command Error");
            }
        }
        System.out.println(result);
    }

    public String handleNewIdentityMessage(Map<String,String> map, Client client){
        String identity = map.get("identity");
        String former = map.get("former");
        String result = "";
        if(former.length()==0){//first time to get identity
            result = "Connected to " + client.getServerConnection().gethostname() + " as " + identity;
            //make change to local client object
            client.setId(map.get("identity"));
            client.setFormerId(map.get("former"));
        }else{
            if(former.equals(client.getId())){//current client change identity
                if(client.getId().equals(identity)){//changeidentity fails
                    result = "Requested identity invalid or in use";
                }else{
                    result = map.get("former")+" is now "+map.get("identity");
                }

                //make change to local client object
                client.setId(map.get("identity"));
                client.setFormerId(map.get("former"));
                client.setStatus(Constants.COMMON_STATUS);
            }else{//other identity change identity
                result =map.get("former")+" is now "+map.get("identity");
            }

        }
        return result;
    }

    public String handleRoomChangeMessage(Map<String,String> map, Client client){
        String identity = map.get("identity");
        String former = map.get("former");
        String roomid = map.get("roomid");
        String result = "";

        if(identity.equals(client.getId())){//if the current client is the one who changes room
            if(roomid.length()==0){//current client quit from server
                result = identity + " leaves " + client.getRoomId();
                result += "\nDisconnected from " + client.getServerConnection().gethostname();
                client.setStatus(Constants.CLOSE);
            }else if(former.equals(roomid)){//current client fails to join
                result = "The request room is invalid or nonexistent";
                client.setStatus(Constants.COMMON_STATUS);
            }else{//current client joins successfully
                if(roomid.equals("MainHall")){
                    client.setStatus(Constants.WAIT_LIST_STATUS);
                }else{
                    client.setStatus(Constants.WAIT_LIST_STATUS);
                    // result = identity + " moves from "+ former +" to "+roomid;
                }
                result = identity + " moves from "+ former +" to "+roomid;
                //update local client variables
                client.setFormerRoomId(client.getRoomId());
                client.setRoomId(roomid);
                //client.setStatus(Constants.COMMON_STATUS);
            }
        }else{//if the current client is not the one who changes room
            if(roomid.length()==0){//other client quit from server
                result = identity + " leaves " + client.getRoomId();
            }else if(former.length()==0){//other client fails to join
                result = identity + " moves to " + roomid;
            }else{//other client joins successfully
                result = identity + " moves from "+ former +" to "+roomid;
            }
        }

        return result;
    }

    public String handleRoomContentsMessage(String JsonMessage){
        Gson gson = new Gson();
        RoomContentsMessage RCM = gson.fromJson(JsonMessage,RoomContentsMessage.class);

        String identities = "";
        for(String identity:RCM.getIdentities()){
            if(identity.equals(RCM.getOwner())){
                identities += identity+"* ";
            }else{
                identities += identity+" ";
            }

        }
        String result = RCM.getRoomid() +" contains " + identities;
        return result;
    }

/*    public String handleRoomListMessage(Client client,String JsonMessage){
        String result = "";
        Gson gson = new Gson();
        RoomListMessage RLM = gson.fromJson(JsonMessage,RoomListMessage.class);
        if(client.isWaiting()){
            System.out.println("room " + client.getTempRoomName() + " created");
            //client.setWaiting(false);

            result = UpdateLocalRoomList(client,RLM);
            client.setTempRoomName(null);
            client.setQuietMode(false);
        }else{
            result = UpdateLocalRoomList(client,RLM);
        }

        return result.trim();
    }*/

    public String handleRoomListMessage(Client client,String JsonMessage){
        String result = "";
        Gson gson = new Gson();
        RoomListMessage RLM = gson.fromJson(JsonMessage,RoomListMessage.class);
        if(client.getStatus()!=null){
            switch(client.getStatus()){
                case "wait1"://wait for create room response
                    boolean success = CreateRoomSuccess(client,RLM);
                    if(success){
                        result = "room " + client.getTempRoomName() + " created";
                    }else{
                        result = "room " + client.getTempRoomName() + " is invalid or already used";
                    }
                    UpdateLocalRoomList(client,RLM);
                    client.setTempRoomName(null);
                    client.setStatus(Constants.COMMON_STATUS);//finished, back to commom status
                    break;

                case "wait2"://wait for delete room response
                    boolean success2 = DeleteRoomSuccess(client,RLM);

                    if(success2){
                        result = "delete " + client.getTempRoomName() + " success";
                    }else{
                        result = "delete " + client.getTempRoomName() + " fails";
                    }

                    UpdateLocalRoomList(client,RLM);
                    client.setStatus(Constants.COMMON_STATUS);
                    client.setTempRoomName(null);
                    //client.setStatus(Constants.COMMON_STATUS);
                    break;

                case "wait3"://wait for list request response
                    //client.setQuietMode(true);//no output needed
                    UpdateLocalRoomList(client,RLM);
                    client.setStatus(Constants.COMMON_STATUS);
                    break;
                case "commonstatus":
                    result = UpdateLocalRoomList(client,RLM);
                    client.setStatus(Constants.COMMON_STATUS);
                    break;
                default:
                    UpdateLocalRoomList(client,RLM);
            }
        }else{
            result = UpdateLocalRoomList(client,RLM);
            client.setStatus(Constants.COMMON_STATUS);
        }


        return result.trim();
    }


    private String UpdateLocalRoomList(Client client, RoomListMessage RLM){
        ArrayList<String> temp_room_list = new ArrayList<>();
        String result = "";
        for(RoomDTO RDTO:RLM.getRooms()){
            temp_room_list.add(RDTO.getRoomid());//add roomlist to local room_list variable
            client.setRoomlist(temp_room_list);
            result += RDTO.getRoomid()+": "+RDTO.getCount()+" guests\n";

        }
        return result.trim();
    }

    private boolean CreateRoomSuccess(Client client, RoomListMessage RLM){
        ArrayList<String> newlist = new ArrayList<>();
        for(RoomDTO RDTO:RLM.getRooms()){
            newlist.add(RDTO.getRoomid());
        }
        ArrayList<String> oldlist = client.printRoomList();
        if(newlist.contains(client.getTempRoomName()) && !oldlist.contains(client.getTempRoomName())){
            return  true;
        }else{
            return  false;
        }
    }

    private boolean DeleteRoomSuccess(Client client, RoomListMessage RLM){
        ArrayList<String> newlist = new ArrayList<>();
        for(RoomDTO RDTO:RLM.getRooms()){
            newlist.add(RDTO.getRoomid());//add roomlist to local room_list variable
        }
        ArrayList<String> oldlist = client.printRoomList();
        if(!newlist.contains(client.getTempRoomName()) && oldlist.contains(client.getTempRoomName())){
            return  true;
        }else{
            return  false;
        }
    }






}
