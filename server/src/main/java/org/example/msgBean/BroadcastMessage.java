package org.example.msgBean;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Xiaotian
 * @program assignment1
 * @description
 * @create 2021-08-18 00:40
 */
@NoArgsConstructor
@Data
@ToString
public class BroadcastMessage {

    private String type = "message";
    private String identity;
    private String content;
}