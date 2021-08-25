package org.example.utils;

import java.nio.charset.StandardCharsets;

/**
 * @author Xiaotian
 * @program assignment1
 * @description UTF8 encoders
 * @create 2021-08-18 23:45
 */
public class Encoders {
    public static String StringToUtf8(String str) {
        return new String(str.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    }
}