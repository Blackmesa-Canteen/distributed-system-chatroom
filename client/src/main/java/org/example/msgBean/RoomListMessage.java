package org.example.msgBean;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.utils.Constants;

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
    private String type = Constants.ROOM_LIST_JSON_TYPE;
    private List<RoomDTO> rooms;

}