package org.example.app;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 *
 * chat room server
 * @author Xiaotian
 * @program assignment1
 * @description app
 * @create 2021-08-17 23:30
 */
public class ServerApp {

    private static int port = 4444;

    public static void main(String[] args) {

        // handle input args -p portNumber
        handleArgs(args);
        System.out.println("port: " + port);

        BackendServer instance = new BackendServer(port);

        instance.start();

        for(;;) {
            // check something
            // if went wrong, instance.close(); break;
        }
        // System.exit(-1);
    }


    private static void handleArgs(String[] args) {
        MyCmdOption option = new MyCmdOption();
        CmdLineParser cmdLineParser = new CmdLineParser(option);

        try {
            if (args.length == 0) {
                // no args, default port is 4444
                port = 4444;

            } else {
                cmdLineParser.parseArgument(args);
                if (option.port < 1 || option.port > 65535) {
                    // port too large
                    System.out.println("port should in [1,65535]");
                    System.exit(-1);
                }

                port = (int) option.port;
            }
        } catch (CmdLineException e) {
            System.out.println("Command line error: " + e.getMessage());
            argHelpInfo(cmdLineParser);
            System.exit(-1);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static void argHelpInfo(CmdLineParser cmdLineParser) {
        System.out.println("-p [port number 1~65535]");
        cmdLineParser.printUsage(System.out);
    }
}