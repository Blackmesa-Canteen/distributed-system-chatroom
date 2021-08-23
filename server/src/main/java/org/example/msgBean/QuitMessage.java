package org.example.msgBean;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.utils.Constants;

/**
 * @author Xiaotian
 * @program assignment1
 * @description
 * @create 2021-08-18 00:40
 */

@Data
@NoArgsConstructor
@ToString
public class QuitMessage {
    private String type = Constants.QUIT_JSON_TYPE;
}