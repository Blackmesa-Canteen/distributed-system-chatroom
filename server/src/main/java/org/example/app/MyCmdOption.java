package org.example.app;

import org.kohsuke.args4j.Option;

/**
 * @author Xiaotian
 * @program assignment1
 * @description
 * @create 2021-08-18 19:59
 */
public class MyCmdOption {

    @Option(name="-p", usage = "server listening port number.")
    public long port = 4444;
}