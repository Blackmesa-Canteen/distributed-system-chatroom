package org.example.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author Xiaotian
 * @program assignment1
 * @description
 * @create 2021-08-18 00:30
 */
@NoArgsConstructor
@Data
@ToString
public class RoomListMessage {
    private String type = "roomlist";
    private List<RoomDTO> rooms;

}