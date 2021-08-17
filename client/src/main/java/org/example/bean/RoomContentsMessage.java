package org.example.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author Xiaotian
 * @program assignment1
 * @description
 * @create 2021-08-18 00:23
 */

@NoArgsConstructor
@ToString
@Data
public class RoomContentsMessage {

    private String type = "roomcontents";
    private String roomid;
    private List<String> identities;
    private String owner;
}