package org.example.app;

import org.example.network.ServerConnection;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.example.pojo.Client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author Xiaotian
 * @program assignment1
 * @description app
 * @create 2021-08-17 23:30
 */
public class ClientApp {

    private static int port = 4444;
    private static String hostname = "localhost";

    public static void main(String[] args) {

        handleArgs(args);
        // already get hostname and port.
        System.out.println();

        boolean connection_alive = false;

        //Default message is null
        String message = "";
        //Create console Scanner
        Scanner in = new Scanner(System.in);
        //Create Client Object to store info
        Client client = new Client();
        //Start connection
        try{
            Socket s = new Socket(hostname,port);
            connection_alive = true;
            org.example.network.ServerConnection conn = new ServerConnection(s,client);
            conn.start();
            client.setServerConnection(conn);
            //System.out.println("Connected to "+hostname);

            //set maximum waiting time for connection
            Thread.sleep(1000);

            //listen on console Scanner
            while(conn.isalive()){
                Thread.sleep(1000);//set maximum waiting time for connection
                if(conn.isalive()){
                    //get input message from console
                    System.out.println("["+client.getRoomId()+"] "+client.getId()+">");
                    message = in.nextLine();
                    conn.SendMessage(message,client);
                }else{
                    conn.close();
                }
            }

        }catch(IOException e){
            System.out.println(e.getMessage());
        }catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }




    }

    private static void handleArgs(String[] args) {
        MyCmdOption option = new MyCmdOption();
        CmdLineParser cmdLineParser = new CmdLineParser(option);

        // handle args
        if (args.length == 0) {
            // host should be defined
            System.out.println("missing server host.");
            System.exit(1);
        }

        try {
            cmdLineParser.parseArgument(args);
            // check validity
            String ipRegex = "^((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)$";
            if (option.hostname == null
                    || option.hostname.equals("") || (!option.hostname.equals("localhost") && !option.hostname.matches(ipRegex))) {
                System.out.println("invalid hostname");
                System.exit(-1);
            }

            if (option.port < 1 || option.port > 65535) {
                // port too large
                System.out.println("port should in [1,65535]");
                System.exit(-1);
            }

            // get args
            hostname = option.hostname;
            port = (int) option.port;

        } catch (CmdLineException e) {
            System.out.println("Command line error: " + e.getMessage());
            argHelpInfo(cmdLineParser);
            System.exit(-1);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(-1);
        }
    }

    private static void argHelpInfo(CmdLineParser cmdLineParser) {
        System.out.println("[hostname] -p [port number 1~65535]");
        cmdLineParser.printUsage(System.out);
    }



}