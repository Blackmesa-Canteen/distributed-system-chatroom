package org.example.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.network.ServerConnection;

import java.time.temporal.Temporal;
import java.util.ArrayList;

/**
 * @author Xiaotian
 * @program assignment1
 * @description
 * @create 2021-08-18 19:37
 */

@Data
@NoArgsConstructor
@ToString
public class Client {
    private String id;
    private String formerId;
    private String formerRoomId;
    private String roomId;
    private ServerConnection serverConnection;
    private ArrayList<String> roomlist;
    private String tempRoomName;
    private boolean waiting = false;
}