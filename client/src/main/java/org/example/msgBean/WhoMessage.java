package org.example.msgBean;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.utils.Constants;

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

    private String type = Constants.WHO_JSON_TYPE;
    private String roomid;
}