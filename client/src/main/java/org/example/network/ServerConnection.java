package org.example.network;

import org.example.pojo.Client;
import org.example.utils.Encoders;
import org.example.utils.JsonEncoder;
import org.example.utils.Reciever;

import java.io.*;
import java.net.Socket;


public class ServerConnection extends Thread{

    private Socket socket;
    private Client client;
    private PrintWriter writer;
    private BufferedReader reader;
    private static JsonEncoder JE = new JsonEncoder();
    private static Encoders En = new Encoders();
    private boolean connection_alive = false;

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
        Reciever reciever = new Reciever();
        while(connection_alive){
            try{
                String in = reader.readLine();
                if(in != null){
                    reciever.handle(in,client);
                    System.out.println(in);
                }else{
                    connection_alive = false;
                }
            }catch(IOException e){
                connection_alive = false;
                System.out.println(e.getMessage());
            }

        }
        close();

    }


    public void SendMessage(String message,Client client){
        if(message!=null && !message.equals("")){
            boolean isCommand = JE.isCommand(message);
            if(isCommand){
                String JsonMessage = En.StringToUtf8(JE.Encode(message,client));//Encode command to json UTF8
                writer.println(JsonMessage);
                writer.flush();
            }else{
                String JsonMessage = En.StringToUtf8(JE.EncodeMessage(message));//Encode message to json UTF8
                writer.println(JsonMessage);
                writer.flush();
            }
        }
    }

    public void close(){
        try{
            socket.close();
            reader.close();
            writer.close();
        }catch(IOException e){
            System.out.println(e.getMessage());
        }

    }

    public String gethostname(){
        return socket.getInetAddress().toString();
    }

}
