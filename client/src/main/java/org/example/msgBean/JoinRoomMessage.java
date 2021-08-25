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
 * @create 2021-08-18 00:16
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JoinRoomMessage {
    private String type = Constants.JOIN_JSON_TYPE;
    private String roomid;
}