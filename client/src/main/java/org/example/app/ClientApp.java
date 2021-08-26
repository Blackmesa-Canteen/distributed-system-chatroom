package org.example.app;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.net.InetAddress;
import java.net.UnknownHostException;

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