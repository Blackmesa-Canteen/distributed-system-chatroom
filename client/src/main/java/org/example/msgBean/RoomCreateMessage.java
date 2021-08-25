package org.example.msgBean;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.utils.Constants;

/**
 * @author Xiaotian
 * @program assignment1
 * @description
 * @create 2021-08-18 00:35
 */
@NoArgsConstructor
@Data
@ToString
public class RoomCreateMessage {

    private String type = Constants.CREATE_ROOM_JSON_TYPE;
    private String roomid;
}