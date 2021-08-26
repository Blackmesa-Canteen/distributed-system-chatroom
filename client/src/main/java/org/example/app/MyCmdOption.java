package org.example.app;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 * @author Xiaotian
 * @program assignment1
 * @description
 * @create 2021-08-18 19:59
 */
public class MyCmdOption {

    @Argument(usage = "server's host name")
    public String hostname = "127.0.0.1";

    @Option(name="-p", usage = "server's listening port number.")
    public long port = 4444;
}