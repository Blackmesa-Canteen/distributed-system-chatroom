package org.example.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Xiaotian
 * @program assignment1
 * @description
 * @create 2021-08-18 00:38
 */
@NoArgsConstructor
@Data
@ToString
public class RoomDeleteMessage {

    private String type = "delete";
    private String roomid;
}