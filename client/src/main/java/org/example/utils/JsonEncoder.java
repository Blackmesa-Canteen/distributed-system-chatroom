package org.example.utils;

import com.google.gson.Gson;
import org.example.msgBean.*;
import org.example.pojo.Client;

import java.util.HashMap;
import java.util.Map;

public class JsonEncoder {

    public boolean isCommand (String message){
        //if the input String starts with #, return true
        return(message.substring(0,1).equals("#"));
    }

    public String Encode(String command, Client client){
        //spilt input command
        String [] arr = command.substring(1).split("\\s+");
        //Gson object
        Gson gson = new Gson();
        String result="";

        switch(arr[0]){//switch based on type
            case "join":
                if(arr.length==1){//if the input command only contains #join with no following arguments
                    System.out.println("invalid command, join option needs 1 argument");
                    break;
                }else if(arr.length == 2){//correct command paradigm
                    JoinRoomMessage JRM = new JoinRoomMessage();
                    JRM.setRoomid(arr[1]);
                    result = gson.toJson(JRM);
                    break;
                }else{//other unconsidered situation
                    System.out.println("command error");
                    break;
                }

            case "identitychange":
                if(arr.length == 1){
                    System.out.println("invalid command, identitychange option needs 1 argument");
                    break;
                }else if(arr.length == 2){
                    IdentityChangeMessage IDchange = new IdentityChangeMessage();
                    IDchange.setIdentity((arr[1]));
                    result = gson.toJson(IDchange);
                    break;
                }else{
                    System.out.println("command error");
                    break;
                }

            case "who":
                if(arr.length == 1){
                    System.out.println("invalid command, who option needs 1 argument");
                    break;
                }else if(arr.length ==2){
                    WhoMessage WM = new WhoMessage();
                    WM.setRoomid(arr[1]);
                    result = gson.toJson(WM);
                    break;
                }else{
                    System.out.println("command error");
                    break;
                }

            case "createroom":
                if(arr.length == 1){
                    System.out.println("invalid command, createroom option needs 1 argument");
                    break;
                }else if(arr.length == 2){
                    StringVerifier SV = new StringVerifier();
                    for(String roomname:client.getRoomlist()){
                        if(roomname.equals(arr[1]) || !SV.isValidRoomId(arr[1])){//check if the roomname has already exists or invalid
                            System.out.println("room " + arr[1] + " is invalid or already in use");
                            return "";
                        }
                    }
                    RoomCreateMessage RCM = new RoomCreateMessage();
                    RCM.setRoomid(arr[1]);
                    result = gson.toJson(RCM);
                    System.out.println("in Jsonencoder setwaiting true");
                    client.setWaiting(true);
                    client.setTempRoomName(arr[1]);
                    break;
                }else{
                    System.out.println("command error");
                    break;
                }

            case "delete":
                if(arr.length == 1){
                    System.out.println("invalid command, deleteroom option needs 1 argument");
                    break;
                }else if(arr.length == 2){
                    RoomDeleteMessage RDM = new RoomDeleteMessage();
                    RDM.setRoomid(arr[1]);
                    result = gson.toJson(RDM);
                    break;
                }else{
                    System.out.println("command error");
                    break;
                }

            case "message":
                System.out.println("message should not start with #");
                break;

            case "quit":
                if(arr.length == 1){
                    QuitMessage QM = new QuitMessage();
                    result = gson.toJson(QM);
                    break;
                }else{
                    System.out.println("quit command error");
                    break;
                }

            default:
                System.out.println("No such command");
        }
        return result;
    }

    public Map Decode(String JsonMessage){
        //Gson object
        Gson gson = new Gson();
        Map<String,String> result= gson.fromJson(JsonMessage, HashMap.class);
        return result;
    }

    public String EncodeMessage(String message){
        Gson gson = new Gson();
        BroadcastMessage Message = new BroadcastMessage();
        Message.setContent(message);
        return gson.toJson(Message);
    }



}
