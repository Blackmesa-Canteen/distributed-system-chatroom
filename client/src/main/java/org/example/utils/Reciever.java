package org.example.utils;

import com.google.gson.Gson;

import java.util.*;

import org.example.msgBean.*;
import org.example.pojo.Client;

public class Reciever {
    private static final JsonEncoder JE = new JsonEncoder();

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
        }else{
            //System.out.println("such message handler haven't finished yet");
        }
    }

    public String handleMessage(Map<String,String> map){
        return (map.get("identity")+": "+map.get("content"));
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
                result = map.get("former")+" is now "+map.get("identity");
                //make change to local client object
                client.setId(map.get("identity"));
                client.setFormerId(map.get("former"));
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

        if(roomid.length()==0){//quit response from server
            result = identity + " leaves " + client.getRoomId();
            if(identity.equals(client.getId())){//if it is the current client who quits from server
                result += "\nDisconnected from localhost";
            }
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


    public String handleRoomListMessage(Client client,String JsonMessage){
        String result = "";
        Gson gson = new Gson();
        RoomListMessage RLM = gson.fromJson(JsonMessage,RoomListMessage.class);
        ArrayList<String> temp_room_list = new ArrayList<>();
        if(client.isWaiting()){
            System.out.println("room " + client.getTempRoomName() + " created");
            client.setWaiting(false);
            client.setTempRoomName(null);
            for(RoomDTO RDTO:RLM.getRooms()){
                temp_room_list.add(RDTO.getRoomid());//add roomlist to local room_list variable
                client.setRoomlist(temp_room_list);
                //result += RDTO.getRoomid()+": "+RDTO.getCount()+" guests\n";
            }
        }else{
            for(RoomDTO RDTO:RLM.getRooms()){
                temp_room_list.add(RDTO.getRoomid());//add roomlist to local room_list variable
                client.setRoomlist(temp_room_list);
                result += RDTO.getRoomid()+": "+RDTO.getCount()+" guests\n";
            }
        }
        return result.trim();
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
                break;
            case "roomlist":
                result = handleRoomListMessage(client,JsonMessage);
                break;
            default:
                System.out.println("Command Error");
        }
        return result;
    }

}
