package org.example.msgBean;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.utils.Constants;

/**
 * @author Xiaotian
 * @program assignment1
 * @description
 * @create 2021-08-18 00:36
 */
@Data
@NoArgsConstructor
@ToString
public class ListMessage {
    private String type = Constants.LIST_JSON_TYPE;
}