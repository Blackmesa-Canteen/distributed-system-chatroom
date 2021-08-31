package org.example.utils;

import com.google.gson.Gson;
import org.example.msgBean.*;

import java.util.HashMap;
import java.util.Map;

public class JsonEncoder {

    public boolean isCommand (String message){
        //if the input String starts with #, return true
        return(message.substring(0,1).equals("#"));
    }

    public String Encode(String command){
        //spilt imput command
        String [] arr = command.substring(1).split("\\s+");
        //Gson object
        Gson gson = new Gson();
        String result="";
        //Constants object
        //switch based on type
        switch(arr[0]){
            case "join":
                JoinRoomMessage JRM = new JoinRoomMessage();
                JRM.setRoomid(arr[1]);
                result = gson.toJson(JRM);
                break;
            case "newidentity":
                break;
            case "identitychange":
                IdentityChangeMessage IDchange = new IdentityChangeMessage();
                IDchange.setIdentity((arr[1]));
                result = gson.toJson(IDchange);
                break;
            case "roomchange":
                //RoomChangeMessage RCM = new RoomChangeMessage();
                break;
            case "roomcontents":
                break;
            case "who":
                WhoMessage WM = new WhoMessage();
                WM.setRoomid(arr[1]);
                result = gson.toJson(WM);
                break;
            case "roomlist":
                break;
            case "list":
                ListMessage LM = new ListMessage();
                result = gson.toJson(LM);
                break;
            case "createroom":
                RoomCreateMessage RCM = new RoomCreateMessage();
                RCM.setRoomid(arr[1]);
                result = gson.toJson(RCM);
                break;
            case "delete":
                RoomDeleteMessage RDM = new RoomDeleteMessage();
                RDM.setRoomid(arr[1]);
                result = gson.toJson(RDM);
                break;
            case "message":
                System.out.println("message should not start with #");
                break;
            case "quit":
                QuitMessage QM = new QuitMessage();
                result = gson.toJson(QM);
                break;
            default:
                System.out.println("No such command");
        }
        if(result == ""){
            System.out.println("command error");
            //System.exit(-1);
        }
        return result;

    }

    public Map Decode(String JsonMessage){
        //Gson object
        Gson gson = new Gson();
        Map<String,String> result= gson.fromJson(JsonMessage, HashMap.class);
        return result;
    }

    public String EncodeMessage(String message,String identity){
        Gson gson = new Gson();
        String result = "";
        BroadcastMessage Message = new BroadcastMessage();
        //Message.setIdentity(identity);
        Message.setContent(message);
        result = gson.toJson(Message);
        return result;
    }



}
