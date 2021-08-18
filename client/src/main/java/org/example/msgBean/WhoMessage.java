package org.example.msgBean;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Xiaotian
 * @program assignment1
 * @description
 * @create 2021-08-18 00:29
 */
@NoArgsConstructor
@Data
@ToString
public class WhoMessage {

    private String type = "who";
    private String roomid;
}