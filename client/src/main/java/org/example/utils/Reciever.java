package org.example.utils;

import com.google.gson.Gson;
import java.util.*;

import org.example.msgBean.*;
import org.example.pojo.Client;
import org.example.pojo.Room;

public class Reciever {
    private static final JsonEncoder JE = new JsonEncoder();

    public void handle(String JsonMessage, Client client){
        Map<String,String> map  =  JE.Decode(JsonMessage);//decode message to map
        String result = "";
        if(map.get("type").equals(Constants.MESSAGE_JSON_TYPE)){//identify message or command
            result += handleMessage(map,client);
        }else{
            result += handleCommand(map,client,JsonMessage);
        }


        if(!result.equals("")){ //&& header!=null && header.length()!=0
            System.out.println(result);
        }else{
            //System.out.println("such message handler haven't finished yet");
        }
    }

    public String handleMessage(Map<String,String> map,Client client){
        return ("["+ client.getRoomId()+"] "+map.get("identity")+": "+map.get("content"));
    }

    public String handleIdentityChangeMessage(Map<String,String> map,Client client){
        return map.get("identity");
    }

    public void handleJoinRoomMessage(Map<String,String> map,Client client){}

    public void handleListMessage(Map<String,String> map,Client client){}

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
                result = map.get("former")+"is now "+map.get("identity");
                //make change to local client object
                client.setId(map.get("identity"));
                client.setFormerId(map.get("former"));
            }else{//other identity change identity
                result =map.get("former")+"is now "+map.get("identity");
            }

        }
        return result;
    }

    public void handleQuitMessage(){}

    public void handleRelayMessage(Map<String,String> map, Client client){}

    public String handleRoomChangeMessage(Map<String,String> map, Client client){
        String identity = map.get("identity");
        String former = map.get("former");
        String roomid = map.get("roomid");
        String result = "";

        if(roomid.length()==0){//quit response from server
            result = identity + " leaves " + client.getRoomId();
            return result;
        }

        if(identity.equals(client.getId())){
            client.setFormerRoomId(client.getRoomId());
            client.setRoomId(roomid);
        }
        if(former.length()==0){
            result = identity + " moves to " + roomid;
        }else{
            result = identity + " moves from "+ former +" to "+roomid;
        }

        return result;
    }

    public String handleRoomContentsMessage(Map<String,String> map, Client client,String JsonMessage){
        Gson gson = new Gson();
        RoomContentsMessage RCM = gson.fromJson(JsonMessage,RoomContentsMessage.class);

        //List<String> identities = RCM.getIdentities();
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

    public void handleRoomCreateMessage(){

    }

    public void handleRoomDeleteMessage(){

    }
    public void handleRoomDTO(){

    }


    public String handleRoomListMessage(Map<String,String> map, Client client,String JsonMessage){
        Gson gson = new Gson();
        RoomListMessage RLM = gson.fromJson(JsonMessage,RoomListMessage.class);
        String available_rooms = "";
        for(RoomDTO RDTO:RLM.getRooms()){
            available_rooms += RDTO.getRoomid()+": "+RDTO.getCount()+"\n";
        }
        //String result = "available rooms: " + available_rooms;
        return available_rooms.trim();
    }

    public void handleWhoMessage(){

    }

    public void handleRoomListMessage(Map<String,String> map, Room room){

    }

    public String handleCommand(Map<String,String> map, Client client,String JsonMessage){
        String type = map.get("type");
        String result = "";
        switch(type){
            case "join":
                break;
            case "newidentity":
                result = handleNewIdentityMessage(map,client);
                break;
            case "identitychange":
                result = handleIdentityChangeMessage(map,client);
                break;
            case "roomchange":
                result = handleRoomChangeMessage(map,client);
                break;
            case "roomcontents":
                result = handleRoomContentsMessage(map,client,JsonMessage);
                break;
            case "who":
                break;
            case "roomlist":
                result = handleRoomListMessage(map,client,JsonMessage);
                break;
            case "list":
                break;
            case "createroom":
                break;
            case "delete":
                break;
            case "message":
                break;
            case "quit":
                break;
            default:
                System.out.println("Command Error");
        }
        return result;
    }

}
