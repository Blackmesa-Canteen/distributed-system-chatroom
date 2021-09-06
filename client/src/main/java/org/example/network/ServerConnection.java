package org.example.network;

import org.example.pojo.Client;
import org.example.utils.*;



import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class ServerConnection extends Thread{

    private Socket socket;
    private Client client;
    private PrintWriter writer;
    private BufferedReader reader;
    private static JsonEncoder JE = new JsonEncoder();
    private static Encoders En = new Encoders();
    private boolean connection_alive = false;
    private Scanner in = new Scanner(System.in);

    public ServerConnection(Socket socket, Client client)throws IOException {
        this.socket = socket;
        this.client = client;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(socket.getOutputStream());
        //this.reader = new DataInputStream(socket.getInputStream());
        //this.writer = new DataOutputStream(socket.getOutputStream());
    }

    public boolean isalive(){
        return connection_alive;
    }

    @Override
    public void run(){
        connection_alive = true;
        Scanner Systeminput = new Scanner(System.in);
        Handler handler = new Handler();
        while(connection_alive){
            try{
/*                if(client.getStatus().equals(Constants.COMMON_STATUS)){
                    System.out.println("["+client.getRoomId()+"] "+client.getId()+">");
                    String message = Systeminput.nextLine();
                    while(!SendMessage(message,client)){//if the input is invalid, try to get input again
                        System.out.println("["+client.getRoomId()+"] "+client.getId()+">");
                        message = Systeminput.nextLine();
                    }
                }*/


                String in = reader.readLine();
                //System.out.println("MEssage from server" + in);
                //System.out.println("current status "+client.getStatus());
                switch (client.getStatus()){
                    case "start"://START_STATUS
                        if(in != null){
                            handler.Initialize(in,client);
                        }
                        //System.out.println("start finish, current client status is " + client.getStatus());
                        break;
                    case "commonstatus"://COMMON_STATUS
                        if(in != null){
                            handler.handle(in,client);
                        }
                        //System.out.println("commonstatus finish, current client status is " + client.getStatus());
                        break;
                    case "wait1"://WAIT_CREATE_STATUS
                        if(in != null){
                            handler.waitCreateResponse(in,client);
                        }
                        //System.out.println("waitCreateResponse finish, current client status is " + client.getStatus());
                        break;
                    case "wait2"://WATI_DELETE_STATUS
                        if(in != null){
                            handler.waitDeleteResponse(in,client);
                        }
                        //System.out.println("WAITdelete response finish, current client status is " + client.getStatus());
                        break;
                    case "wait3"://WAIT_LIST_STATUS
                        if(in !=null){
                            handler.waitListResponse(in,client);
                        }
                        //System.out.println("Wait List status finish, current client status is " + client.getStatus());
                        break;
                    case "wait4"://WAIT_QUIT_STATUS
                        if(in !=null){
                            handler.waitQuitResponse(in,client);
                        }
                        //System.out.println("wait quit status finish, current client status is " + client.getStatus());
                        break;
                    case "wait5"://WAIT_IDCHANGE_STATUS
                        if(in !=null){
                            handler.waitIDChangeResponse(in,client);
                        }
                        //System.out.println("wait IDCHANGE finish, current client status is " + client.getStatus());
                        break;
                    case "wait6"://WAIT_JOIN_STATUS
                        if(in !=null){
                            handler.waitJoinResponse(in,client);
                        }
                        //System.out.println("wAIT JOIN STATUS finish, current client status is " + client.getStatus());
                        break;
                    case "close"://CLOSE_STATUS
                        close();
                        System.exit(0);
                        break;
                }
            }catch(IOException e){
                System.out.println(e.getMessage());
                close();
                System.exit(100);
            }

        }

    }


    public boolean SendMessage(String message,Client client){
        if(message!=null && !message.equals("")){
            boolean isCommand = JE.isCommand(message);
            if(isCommand){
                String EncodedCommand = JE.Encode(message,client);
                if(EncodedCommand==""){
                    return false;
                }else{
                    String JsonMessage = En.StringToUtf8(JE.Encode(message,client));//Encode command to json UTF8
                    writer.println(JsonMessage);
                    writer.flush();
                }

            }else{
                String JsonMessage = En.StringToUtf8(JE.EncodeMessage(message));//Encode message to json UTF8
                writer.println(JsonMessage);
                writer.flush();
            }
        }
        return true;
    }

    public void close(){
        try{
            socket.close();
            reader.close();
            writer.close();
            connection_alive = false;
        }catch(IOException e){
            System.out.println(e.getMessage());
        }

    }

    public String gethostname(){
        return socket.getInetAddress().toString();
    }

}
