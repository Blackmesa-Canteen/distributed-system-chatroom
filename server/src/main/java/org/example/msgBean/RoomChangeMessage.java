package org.example.msgBean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.utils.Constants;

/**
 * @author Xiaotian
 * @program assignment1
 * @description
 * @create 2021-08-18 00:17
 */

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RoomChangeMessage {
    private String type = Constants.ROOM_CHANGE_JSON_TYPE;
    private String identity;
    private String former;
    private String roomid;
}